package ru.practicum.user.model;

import org.modelmapper.ModelMapper;

public class UserMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserDto convertToDto(User request) {
        return modelMapper.map(request, UserDto.class);
    }

    public static User convertToEntity(UserDto requestDto) {
        return modelMapper.map(requestDto, User.class);
    }
}
