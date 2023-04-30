package ru.practicum.event.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.constraint.Update;
import ru.practicum.constraint.UpdateUser;
import ru.practicum.constraint.validator.SortMethod;
import ru.practicum.event.event.model.*;
import ru.practicum.constraint.Create;
import ru.practicum.event.event.model.mapping.EventMapper;
import ru.practicum.request.RequestStatDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RestControllerAdvice
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public EventController(EventService eventService,
                           @Value("${statistics.server_url}") String statsServerUrl,
                           @Value("${statistics.app_name}") String statsAppName) {
        this.eventService = eventService;
        statsClient = new StatsClient(statsServerUrl, statsAppName);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events")
    public EventResponseNewDto addEvent(
            @PathVariable("userId") long userId,
            @RequestBody @Validated(Create.class) EventRequestDto eventDto) {
        Event event = EventMapper.convertToEntity(eventDto);
        event.setStateAction(StateAction.PENDING_EVENT);
        log.info("POST /users/{}/events of: {}", userId, event);
        return EventMapper.convertToNewDto(eventService.addEvent(event, userId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/admin/events")
    public List<EventResponseFullDto> getEventsAdmin(
            @RequestParam(value = "users", defaultValue = "") List<Long> users,
            @RequestParam(value = "states", defaultValue = "") List<String> states,
            @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
            @RequestParam(value = "rangeStart", defaultValue = "1800-01-01 12:12:12")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", defaultValue = "5000-01-01 12:12:12")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GET /admin/events from: {}; size: {}; users: {}; states: {}; categories: {}; start: {}; end: {}",
                from, size, users, states, categories, rangeStart, rangeEnd);
        List<Event> eventsList = eventService.getEvents(from, size, users, states, categories, rangeStart, rangeEnd);
        return eventsList.stream()
                .map(EventMapper::convertToFullDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventResponseFullDto changeEvent(@RequestBody @Validated(Update.class) EventRequestDto eventDto,
                                            @PathVariable(value = "eventId") long eventId) {
        Event event = EventMapper.convertToEntity(eventDto);
        log.info("PATCH /admin/events of: {}; to {}", eventId, event);
        return EventMapper.convertToFullDto(eventService.changeEvent(eventId, event));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventResponseFullDto changeEventByUser(@RequestBody @Validated(UpdateUser.class) EventRequestDto eventDto,
                                                  @PathVariable(value = "userId") long userId,
                                                  @PathVariable(value = "eventId") long eventId) {
        Event event = EventMapper.convertToEntity(eventDto);
        log.info("PATCH /users/{}/events/{}", userId, eventId);
        return EventMapper.convertToFullDto(eventService.changeEventByUser(userId, eventId, event));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/events/{eventId}")
    public EventResponseFullDto getEventOfUser(@PathVariable("eventId") long eventId,
                                               @PathVariable("userId") long userId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        Event event = eventService.getEventOfUser(eventId, userId);
        return EventMapper.convertToFullDto(event);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events")
    public List<EventResponseShortDto> getEventsPublic(
            HttpServletRequest request,
            @RequestParam(value = "text", defaultValue = "") String text,
            @RequestParam(value = "categories", defaultValue = "") List<Long> categories,
            @RequestParam(value = "paid", defaultValue = "true") Boolean paid,
            @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "sort", defaultValue = "") @SortMethod String sort,
            @RequestParam(value = "rangeStart", defaultValue = "1800-01-01 12:12:12")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", defaultValue = "5000-01-01 12:12:12")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
            @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GET /events of: {}; from: {}; size: {}; categories: {}; paid: {}; onlyAvailable: {}; sort: {}; rangeStart: {}; rangeEnd: {}",
                text, from, size, categories, paid, onlyAvailable, sort, rangeStart, rangeEnd);
        try {
            statsClient.addRequest(request.getRequestURI(), request.getRemoteAddr());
        } catch (Exception ignored) {

        }
        List<Event> eventsList = eventService.getEventsPublic(text, from, size, categories, paid, onlyAvailable, sort, rangeStart, rangeEnd);
        Map<String, Long> viewsMap = getViews(eventsList);
        setViews(eventsList, viewsMap);
        if (sort.equals(SortField.VIEWS.name())) {
            Collections.sort(eventsList);
        }
        return eventsList.stream()
                .map(EventMapper::convertToShortDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events/{eventId}")
    public EventResponseFullDto getEventPublic(
            HttpServletRequest request,
            @PathVariable("eventId") long eventId) {
        log.info("GET /admin/events/{}", eventId);
        try {
            statsClient.addRequest(request.getRequestURI(), request.getRemoteAddr());
        } catch (Exception exception) {
            log.info("Stats client exception thrown: {}", exception.getMessage());
        }
        Event event = eventService.getPublishedEventById(eventId);
        Map<String, Long> viewsMap = getViews(List.of(event));
        setViews(List.of(event), viewsMap);
        return EventMapper.convertToFullDto(event);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/events")
    public List<EventResponseShortDto> getEventsOfUser(@PathVariable("userId") long userId,
                                                       @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                       @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GET /users/{userId}/events of: {}; from: {}; size: {}", userId, from, size);
        List<Event> eventsList = eventService.getEventsOfUser(userId, from, size);
        return eventsList.stream()
                .map(EventMapper::convertToShortDto)
                .collect(Collectors.toList());
    }

    private void setViews(List<Event> events, Map<String, Long> viewsMap) {
        Map<String, Event> eventMap = new HashMap<>();
        for (Event event : events) {
            eventMap.put(String.format("/events/%s", event.getId()), event);
        }
        for (String viewKey : viewsMap.keySet()) {
            Event event = eventMap.get(viewKey);
            event.setViews(viewsMap.getOrDefault(viewKey, 0L));
        }
    }

    private Map<String, Long> getViews(List<Event> eventsList) {
        Map<String, Long> viewsMap = new HashMap<>();
        try {
            HttpResponse<String> statResponse = statsClient.getStatistics(
                    LocalDateTime.now().minusYears(10L),
                    LocalDateTime.now().plusDays(1L),
                    eventsList.stream()
                            .map(Event::getId)
                            .map(id -> "/events/" + id)
                            .collect(Collectors.toList()),
                    true
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
}



