package ru.practicum.event.request.model;

import org.modelmapper.ModelMapper;
import ru.practicum.event.request.ParticipationResponseDto;

public class ParticipationMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(new ParticipationDtoConverter());
    }

    public static ParticipationResponseDto convertToDto(ParticipationRequest participationRequest) {
        return modelMapper.map(participationRequest, ParticipationResponseDto.class);
    }

}
