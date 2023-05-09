package ru.practicum.event.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.CategoryService;
import ru.practicum.category.model.Category;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.constants.EventState;
import ru.practicum.event.event.model.QEvent;
import ru.practicum.event.event.model.constants.StateAction;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.location.LocationService;
import ru.practicum.location.model.Location;
import ru.practicum.request.RequestStatDto;
import ru.practicum.user.UserService;

import javax.servlet.http.HttpServletRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final LocationService locationService;
    private final StatsClient statsClient;
    private final CategoryService categoryService;

    @Autowired
    public EventServiceImpl(UserService userService, EventRepository eventRepository, JPAQueryFactory jpaQueryFactory,
                            LocationService locationService,
                            @Value("${statistics.server_url}") String statsServerUrl,
                            @Value("${statistics.app_name}") String statsAppName, CategoryService categoryService) {
        this.userService = userService;
        this.eventRepository = eventRepository;
        this.jpaQueryFactory = jpaQueryFactory;
        this.locationService = locationService;
        this.categoryService = categoryService;
        statsClient = new StatsClient(statsServerUrl, statsAppName);
    }


    @Override
    public Event addEvent(Event event, long initiatorId) {
        event.setInitiator(userService.getUserById(initiatorId));// throws exception if not found
        Location savedLocation = locationService.addLocation(event.getLocation());
        if (event.getCategory() != null) {
            Category savedCategory = categoryService.getCategoryById(event.getCategory().getId());
            event.setCategory(savedCategory);
        }
        event.setLocation(savedLocation);
        event.setCreatedOn(LocalDateTime.now());
        event.setStatusStr(EventState.PENDING.name());

        Event savedEvent = eventRepository.save(event);
        eventRepository.flush();
        Event eventInRepo = getAnyEventById(savedEvent.getId());
        log.info("EventRepository saved: {}", eventInRepo);
        return eventInRepo;
    }

    @Override
    public List<Event> getEvents(int from, int size, List<Long> users, List<String> states,
                                 List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        BooleanExpression inUsers = isInUsers(users);
        BooleanExpression inCategories = isInCategories(categories);
        BooleanExpression inStates = isInStates(states);
        BooleanExpression betweenStartEnd = isBetweenStartEnd(rangeStart, rangeEnd);

        List<Event> foundEvents = IterableUtils.toList(jpaQueryFactory.selectFrom(QEvent.event)
                .where(inUsers)
                .where(inCategories)
                .where(betweenStartEnd)
                .where(inStates)
                .orderBy(QEvent.event.eventDate.asc())
                .offset(from)
                .limit(size)
                .fetch());
        List<Event> eventsWithConfirmedRequests = foundEvents.stream()
                .peek(Event::countConfirmedRequests)
                .collect(Collectors.toList());
        log.info("EventRepository returns: {}", eventsWithConfirmedRequests);
        return eventsWithConfirmedRequests;
    }

    @Override
    public Event changeEvent(long eventId, Event eventChangeTo) {
        Event eventInRepo = getAnyEventById(eventId);
        if (!eventInRepo.getState().equals(EventState.PENDING)) {
            throw new DataIntegrityViolationException(String.format("Event %s cannot be modified", eventId));
        }
        eventChangeTo.setPublishedOn(LocalDateTime.now());
        setStatus(eventChangeTo);
        if (eventChangeTo.getCategory() != null) {
            Category savedCategory = categoryService.getCategoryById(eventChangeTo.getCategory().getId());
            eventChangeTo.setCategory(savedCategory);
        }
        Event mergedEvent = merge(eventInRepo, eventChangeTo);
        log.info("EventRepository had: {}; changing to: {}", eventInRepo, eventChangeTo);
        eventRepository.save(mergedEvent);
        return mergedEvent;
    }

    @Override
    public List<Event> getEventsOfUser(long userId, int from, int size) {
        userService.getUserById(userId);// throws exception if not found
        List<Event> events = IterableUtils.toList(jpaQueryFactory.selectFrom(QEvent.event)
                .where(QEvent.event.initiator.id.eq(userId))
                .orderBy(QEvent.event.eventDate.asc())
                .offset(from)
                .limit(size)
                .fetch());
        return events.stream()
                .peek(Event::countConfirmedRequests)
                .collect(Collectors.toList());
    }

    @Override
    public List<Event> getEventsPublic(String text, int from, int size, List<Long> categories, Boolean isPaid,
                                       Boolean onlyAvailable, String sort, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, HttpServletRequest request) {
        BooleanExpression inCategories = isInCategories(categories);
        BooleanExpression hasText = hasText(text);
        List<Event> events = IterableUtils.toList(jpaQueryFactory.selectFrom(QEvent.event)
                .where(inCategories)
                .where(QEvent.event.paid.eq(isPaid))
                .where(hasText)
                .orderBy(QEvent.event.eventDate.asc())
                .offset(from)
                .limit(size)
                .fetch());
        List<Event> eventsConfirmedRequests = events.stream()
                .peek(Event::countConfirmedRequests)
                .filter(event -> event.getConfirmedRequests() <= event.getParticipantLimit())
                .collect(Collectors.toList());
        for (Event event : eventsConfirmedRequests) {
            addRequest(request.getRequestURI() +  "/" + event.getId(), request.getRemoteAddr());
        }
        addRequest(request);
        getAndSetViews(eventsConfirmedRequests);
        return eventsConfirmedRequests;
    }

    @Override
    public Event getPublishedEventByIdPublic(long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndStatusStr(eventId, EventState.PUBLISHED.name())
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId))
                );
        addRequest(request);
        event.countConfirmedRequests();
        getAndSetViews(List.of(event));
        return event;
    }

    @Override
    public Event getPublishedEventById(long eventId) {
        Event event = eventRepository.findByIdAndStatusStr(eventId, EventState.PUBLISHED.name())
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%s is not published", eventId))
                );
        event.countConfirmedRequests();
        return event;
    }

    @Override
    public Event getPublishedEventByIdConflict(long eventId) { // очередной тупой костыль из-за фантастически нелогичных тестов
        Event event = eventRepository.findByIdAndStatusStr(eventId, EventState.PUBLISHED.name())
                .orElseThrow(() -> new DataIntegrityViolationException(String.format("Event with id=%s was not found", eventId))
                );
        event.countConfirmedRequests();
        return event;
    }

    @Override
    public Event changeEventByUser(long userId, long eventId, Event eventChangeTo) {
        userService.getUserById(userId);// throws exception if not found
        Event eventInRepo = getAnyEventById(eventId);
        boolean allowedToBeChanged = eventInRepo.getState().equals(EventState.PENDING) ||
                eventInRepo.getState().equals(EventState.CANCELED);
        if (!allowedToBeChanged) {
            throw new DataIntegrityViolationException(String.format("Event id=%s cannot be modified", eventId));
        }
        setStatus(eventChangeTo);
        Event mergedEvent = merge(eventInRepo, eventChangeTo);
        log.info("EventRepository had: {}; changing to: {}", eventInRepo, mergedEvent);
        eventRepository.save(mergedEvent);
        return mergedEvent;
    }

    @Override
    public Event getEventOfUser(long eventId, long userId) {
        userService.getUserById(userId);// throws exception if not found
        Event event = getAnyEventById(eventId);
        if (event.getInitiator().getId() == userId) {
            return event.countConfirmedRequests();
        } else {
            throw new ObjectNotFoundException(String.format("User with id=%s is not an initiator of Event with id=%s", userId, eventId));
        }
    }

    @Override
    public List<Event> getAndSetViews(List<Event> eventList) {
        if (eventList.isEmpty()) return null;
        Map<String, Long> getViews = getViews(eventList);
        return setViews(eventList, getViews);
    }

    @Override
    public List<Event> getEventsInCategory(long catId) {
        return eventRepository.findAllByCategoryId(catId);
    }

    public void addRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String ip = request.getRemoteAddr();
        try {
            statsClient.addRequest(uri, ip);
        } catch (Exception e) {
            log.error("Exception - {}", e.getMessage(), e);
        }
    }

    public void addRequest(String uri, String ip) {
        try {
            statsClient.addRequest(uri, ip);
        } catch (Exception e) {
            log.error("Exception - {}", e.getMessage(), e);
        }
    }

    public List<Event> setViews(List<Event> events, Map<String, Long> viewsMap) {
        Map<String, Event> eventMap = new HashMap<>();
        for (Event event : events) {
            eventMap.put(String.format("/events/%s", event.getId()), event);
        }
        for (String viewKey : viewsMap.keySet()) {
            Event event = eventMap.get(viewKey);
            event.setViews(viewsMap.getOrDefault(viewKey, 0L));
        }
        return new ArrayList<>(eventMap.values());
    }

    public Map<String, Long> getViews(List<Event> eventsList) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Long> viewsMap = new HashMap<>();

        try {
            HttpResponse<String> statResponse = statsClient.getStatistics(
                    LocalDateTime.now().minusYears(10L),
                    LocalDateTime.now().plusDays(1L),
                    eventsList.stream()
                            .map(Event::getId)
                            .map(id -> "/events/" + id)
                            .collect(Collectors.toList()),
                    false
            );
            log.info("GET /stats response : {}", statResponse);

            RequestStatDto[] requestStatDto = objectMapper.readValue(statResponse.body(), RequestStatDto[].class);
            viewsMap = Arrays.stream(requestStatDto)
                    .collect(Collectors.toMap(RequestStatDto::getUri, RequestStatDto::getHits));
        } catch (Exception e) {
            log.error("Exception - {}", e.getMessage(), e);
        }
        return viewsMap;
    }

    private Event getAnyEventById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId))
                );
        event.countConfirmedRequests();
        return event;
    }

    public void setStatus(Event event) {
        switch (StateAction.valueOf(event.getStateAction())) {
            case PUBLISH_EVENT:
                event.setState(EventState.PUBLISHED);
                event.setStatusStr(EventState.PUBLISHED.name());
                break;
            case CANCEL_REVIEW:
            case REJECT_EVENT:
                event.setState(EventState.CANCELED);
                event.setStatusStr(EventState.CANCELED.name());
                break;
            case SEND_TO_REVIEW:
                event.setState(EventState.PENDING);
                event.setStatusStr(EventState.PENDING.name());
                break;
        }
    }

    private BooleanExpression hasText(String text) {
        return QEvent.event.annotation.containsIgnoreCase(text)
                .or(QEvent.event.description.containsIgnoreCase(text));
    }

    private BooleanExpression isInStates(List<String> states) {
        return states.isEmpty() ?
                Expressions.asBoolean(true).isTrue() : QEvent.event.statusStr.in(states);
    }

    private BooleanExpression isBetweenStartEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        return rangeStart == null || rangeEnd == null ?
                Expressions.asBoolean(true).isTrue() : QEvent.event.eventDate.between(rangeStart, rangeEnd);
    }

    private BooleanExpression isInCategories(List<Long> categories) {
        return categories.isEmpty() ?
                Expressions.asBoolean(true).isTrue() : QEvent.event.category.id.in(categories);
    }

    private BooleanExpression isInUsers(List<Long> users) {
        return users.isEmpty() ?
                Expressions.asBoolean(true).isTrue() : QEvent.event.initiator.id.in(users);
    }

    public Event merge(Event event, Event other) {
        if (other.getTitle() != null) event.setTitle(other.getTitle());
        if (other.getAnnotation() != null) event.setAnnotation(other.getAnnotation());
        if (other.getDescription() != null) event.setDescription(other.getDescription());
        if (other.getPaid() != null) event.setPaid(other.getPaid());
        if (other.getParticipantLimit() != null) event.setParticipantLimit(other.getParticipantLimit());
        if (other.getRequestModeration() != null) event.setRequestModeration(other.getRequestModeration());
        if (other.getEventDate() != null) event.setEventDate(other.getEventDate());
        if (other.getLocation() != null) event.setLocation(other.getLocation());
        if (other.getCreatedOn() != null) event.setCreatedOn(other.getCreatedOn());
        if (other.getCategory() != null) event.setCategory(other.getCategory());
        if (other.getInitiator() != null) event.setInitiator(other.getInitiator());
        if (other.getStatusStr() != null) event.setStatusStr(other.getStatusStr());
        if (other.getPublishedOn() != null) event.setPublishedOn(other.getPublishedOn());
        if (other.getState() != null) event.setState(other.getState());
        event.setState(EventState.valueOf(event.getStatusStr()));
        return event;
    }
}
