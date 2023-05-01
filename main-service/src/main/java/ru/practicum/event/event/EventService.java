package ru.practicum.event.event;

import ru.practicum.event.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    Event addEvent(Event event, long initiatorId);

    List<Event> getEvents(int from, int size, List<Long> users, List<String> states, List<Long> categories,
                          LocalDateTime rangeStart, LocalDateTime rangeEnd);

    Event changeEvent(long eventId, Event event);

    Event getPublishedEventByIdPublic(long eventId, HttpServletRequest request);

    List<Event> getEventsOfUser(long userId, int from, int size);

    List<Event> getEventsPublic(String text, int from, int size, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                                String sort, LocalDateTime rangeStart, LocalDateTime rangeEnd, HttpServletRequest request);

    Event getEventOfUser(long eventId, long userId);

    Event getPublishedEventById(long eventId);

    Event getPublishedEventByIdConflict(long eventId);

    Event changeEventByUser(long userId, long eventId, Event event);

    List<Event> getAndSetViews(List<Event> eventList);

    List<Event> getEventsInCategory(long catId);
}
