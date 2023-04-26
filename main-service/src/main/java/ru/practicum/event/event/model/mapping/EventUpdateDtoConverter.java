package ru.practicum.event.event.model.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.EventResponseUpdateDto;

public class EventUpdateDtoConverter implements Converter<Event, EventResponseUpdateDto> {
    @Override
    public EventResponseUpdateDto convert(MappingContext<Event, EventResponseUpdateDto> mappingContext) {
        Event source = mappingContext.getSource();
        EventResponseUpdateDto destination = mappingContext.getDestination() == null ? new EventResponseUpdateDto() : mappingContext.getDestination();
        destination.setAnnotation(source.getAnnotation());
        destination.setCategory(source.getCategory().getId());
        destination.setDescription(source.getDescription());
        destination.setEventDate(source.getEventDate());
        destination.setLocation(source.getLocation());
        destination.setParticipantLimit(source.getParticipantLimit());
        destination.setRequestModeration(source.getRequestModeration());
        destination.setPaid(source.getPaid());
        destination.setStateAction(source.getStateAction());
        destination.setTitle(source.getTitle());
        return destination;
    }
}
