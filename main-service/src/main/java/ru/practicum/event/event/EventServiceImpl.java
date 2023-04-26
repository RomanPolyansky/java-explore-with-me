package ru.practicum.event.event;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.QEvent;
import ru.practicum.event.event.model.StateAction;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.location.LocationService;
import ru.practicum.location.model.Location;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final UserService userService;
    private final EventRepository eventRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final LocationService locationService;


    @Override
    public Event addEvent(Event event, long initiatorId) {
        event.setInitiator(userService.getUserById(initiatorId));// throws exception if not found
        Location savedLocation = locationService.addLocation(event.getLocation());
        event.setLocation(savedLocation);
        event.setCreatedOn(LocalDateTime.now());
        event.setStatusStr(StateAction.PENDING_EVENT.name());
        Event savedEvent = eventRepository.save(event);
        log.info("EventRepository saved: {}", savedEvent);
        return savedEvent;
    }

    @Override
    public List<Event> getEvents(int from, int size, List<Long> users,List<String> states,
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
        log.info("EventRepository returns: {}", foundEvents);
        return foundEvents;
    }

    @Override
    public Event changeEvent(long eventId, Event eventChangeTo) {
        Event eventInRepo = getAnyEventById(eventId);
        if (!eventInRepo.getStateAction().equals(StateAction.PENDING_EVENT)) {
            throw new DataIntegrityViolationException(String.format("Event %s cannot be modified", eventId));
        }
        Event mergedEvent = eventInRepo.merge(eventChangeTo);
        log.info("EventRepository had: {}; changing to: {}", eventInRepo, eventChangeTo);
        eventRepository.save(mergedEvent);
        return mergedEvent;
    }

    @Override
    public Event getPublishedEventById(long eventId) {
        return eventRepository.findByIdAndStatusStr(eventId, StateAction.PUBLISH_EVENT.name())
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId))
        );
    }

    @Override
    public List<Event> getEventsOfUser(long userId, int from, int size) {
        userService.getUserById(userId);// throws exception if not found
        return IterableUtils.toList(jpaQueryFactory.selectFrom(QEvent.event)
                .where(QEvent.event.initiator.id.eq(userId))
                .orderBy(QEvent.event.eventDate.asc())
                .offset(from)
                .limit(size)
                .fetch());
    }

    @Override
    public List<Event> getEventsPublic(String text, int from, int size, List<Long> categories, Boolean isPaid,
                                 Boolean onlyAvailable, String sort, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        BooleanExpression inCategories = isInCategories(categories);
        BooleanExpression hasText = hasText(text); // TODO WHERE CONFIRMED REQUEST < PARTICIPANT LIMIT

        return IterableUtils.toList(jpaQueryFactory.selectFrom(QEvent.event)
                .where(inCategories)
                .where(QEvent.event.paid.eq(isPaid))
                .where(hasText)
                .orderBy(QEvent.event.eventDate.asc())
                .offset(from)
                .limit(size)
                .fetch());
    }

    @Override
    public Event getEventOfUser(long eventId, long userId) {
        userService.getUserById(userId);// throws exception if not found
        Event event = getAnyEventById(eventId);
        if (event.getInitiator().getId() == userId) {
            return event;
        } else {
            throw new ObjectNotFoundException(String.format("User with id=%s is not an initiator of Event with id=%s", userId, eventId));
        }
    }

    @Override
    public Event changeEventByUser(long userId, long eventId, Event eventChangeTo) {
        userService.getUserById(userId);// throws exception if not found
        Event eventInRepo = getAnyEventById(eventId);
        boolean allowedToBeChanged = eventInRepo.getStateAction().equals(StateAction.PENDING_EVENT) ||
                eventInRepo.getStateAction().equals(StateAction.CANCEL_REVIEW);
        if (!allowedToBeChanged) {
            throw new DataIntegrityViolationException(String.format("Event id=%s cannot be modified", eventId));
        }
        Event mergedEvent = eventInRepo.merge(eventChangeTo);
        log.info("EventRepository had: {}; changing to: {}", eventInRepo, eventChangeTo);
        eventRepository.save(mergedEvent);
        return mergedEvent;
    }

    private BooleanExpression hasText(String text) {
        return QEvent.event.annotation.containsIgnoreCase(text)
                .or(QEvent.event.description.containsIgnoreCase(text));
    }

    private Event getAnyEventById(long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Event with id=%s was not found", eventId))
                );
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
}
