package ru.practicum.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsClient;
import ru.practicum.constraint.Update;
import ru.practicum.event.model.*;
import ru.practicum.constraint.Create;
import ru.practicum.event.model.mapping.EventMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RestControllerAdvice
public class EventController {

    private final EventService eventService;
    private final StatsClient statsClient;

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
    public List<EventResponseFullDto> getCategories(
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
    public EventResponseUpdateDto addEvent(@RequestBody @Validated(Update.class) EventRequestDto eventDto,
                                    @PathVariable(value = "eventId") long eventId) {
        Event event = EventMapper.convertToEntity(eventDto);
        log.info("PATCH /admin/events of: {}; to {}", eventId, event);
        return EventMapper.convertToUpdateDto(eventService.changeEvent(eventId, event));
    }
//
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @DeleteMapping("/admin/events/{eventId}")
//    public void deleteEvent(@PathVariable(value = "eventId") long eventId) {
//        log.info("DELETE of: {}", eventId);
//        eventService.deleteEvent(eventId);
//    }
//
//    @ResponseStatus(HttpStatus.OK)
//    @GetMapping("/events/{eventId}")
//    public EventRequestDto getEvent(@PathVariable(value = "eventId") Long id) {
//        log.info("GET /events id: {}", id);
//        return EventMapper.convertToDto(eventService.getEventById(id));
//    }
}

























































