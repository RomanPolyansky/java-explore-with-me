package ru.practicum.event.event;

import ru.practicum.event.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event addEvent(Event event, long initiatorId);

    List<Event> getEvents(int from, int size, List<Long> users, List<String> states, List<Long> categories,
                          LocalDateTime rangeStart, LocalDateTime rangeEnd);

    Event changeEvent(long eventId, Event event);

    Event getPublishedEventById(long eventId);

    List<Event> getEventsOfUser(long userId, int from, int size);

    List<Event> getEventsPublic(String text, int from, int size, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                String sort, LocalDateTime rangeStart, LocalDateTime rangeEnd);

    Event getEventOfUser(long eventId, long userId);

    Event changeEventByUser(long userId, long eventId, Event event);
}
