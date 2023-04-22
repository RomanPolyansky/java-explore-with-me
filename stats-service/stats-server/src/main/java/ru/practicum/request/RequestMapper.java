package ru.practicum.request;

import org.modelmapper.ModelMapper;
import ru.practicum.request.entity.Request;

public class RequestMapper {

    private final ModelMapper modelMapper;

    public RequestMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    RequestDto convertToDto(Request request) {
        return modelMapper.map(request, RequestDto.class);
    }

    Request convertToEntity(RequestDto requestDto) {
        return modelMapper.map(requestDto, Request.class);
    }
}
