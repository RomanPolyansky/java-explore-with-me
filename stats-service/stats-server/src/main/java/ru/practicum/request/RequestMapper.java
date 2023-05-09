package ru.practicum.request;

import org.modelmapper.ModelMapper;
import ru.practicum.request.entity.Request;

public class RequestMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static RequestDto convertToDto(Request request) {
        return modelMapper.map(request, RequestDto.class);
    }

    public static Request convertToEntity(RequestDto requestDto) {
        return modelMapper.map(requestDto, Request.class);
    }
}