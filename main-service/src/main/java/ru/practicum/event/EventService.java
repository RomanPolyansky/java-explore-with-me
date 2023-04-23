package ru.practicum.event;

import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event addEvent(Event event, long initiatorId);
    List<Event> getEvents(int from, int size, List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd);

    Event changeEvent(long eventId, Event event);
}
