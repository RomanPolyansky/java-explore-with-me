package ru.practicum.event.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.constraint.Update;
import ru.practicum.constraint.UpdateUser;
import ru.practicum.constraint.validator.SortMethod;
import ru.practicum.event.event.model.*;
import ru.practicum.constraint.Create;
import ru.practicum.event.event.model.constants.EventState;
import ru.practicum.event.event.model.constants.SortField;
import ru.practicum.event.event.model.mapping.EventMapper;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RestControllerAdvice
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events")
    public EventResponseFullDto addEvent(
            @PathVariable("userId") long userId,
            @RequestBody @Validated(Create.class) EventRequestDto eventDto) {
        Event event = EventMapper.convertToEntity(eventDto);
        event.setState(EventState.PENDING);
        log.info("POST /users/{}/events of: {}", userId, event);
        return EventMapper.convertToFullDto(eventService.addEvent(event, userId));
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
        List<Event> eventsList = eventService.getEventsPublic(text, from, size, categories, paid, onlyAvailable, sort, rangeStart, rangeEnd, request);
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
        Event event = eventService.getPublishedEventByIdPublic(eventId, request);
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
}



