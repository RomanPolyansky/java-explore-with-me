package ru.practicum.event.request.model;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import ru.practicum.event.request.ParticipationResponseDto;

public class ParticipationDtoConverter implements Converter<ParticipationRequest, ParticipationResponseDto> {
    @Override
    public ParticipationResponseDto convert(MappingContext<ParticipationRequest, ParticipationResponseDto> mappingContext) {
        ParticipationRequest source = mappingContext.getSource();
        ParticipationResponseDto destination = mappingContext.getDestination() == null ? new ParticipationResponseDto() : mappingContext.getDestination();
        destination.setId(source.getId());
        destination.setStatus(source.getStatus());
        destination.setEvent(source.getEvent().getId());
        destination.setRequester(source.getRequester().getId());
        destination.setCreated(source.getCreated());
        return destination;
    }
}