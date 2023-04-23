package ru.practicum.event.model.mapping;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventResponseNewDto;

public class EventNewDtoConverter implements Converter<Event, EventResponseNewDto> {
    @Override
    public EventResponseNewDto convert(MappingContext<Event, EventResponseNewDto> mappingContext) {
        Event source = mappingContext.getSource();
        EventResponseNewDto destination = mappingContext.getDestination() == null ? new EventResponseNewDto() : mappingContext.getDestination();
        destination.setPaid(source.getPaid());
        destination.setAnnotation(source.getAnnotation());
        destination.setLocation(source.getLocation());
        destination.setEventDate(source.getEventDate());
        destination.setDescription(source.getDescription());
        destination.setTitle(source.getTitle());
        destination.setParticipantLimit(source.getParticipantLimit());
        destination.setRequestModeration(source.getRequestModeration());
        destination.setCategory(source.getCategory().getId());
        return destination;
    }
}
