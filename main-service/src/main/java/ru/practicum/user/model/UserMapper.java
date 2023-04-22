package ru.practicum.user.model;

import org.modelmapper.ModelMapper;

public class UserMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserDto convertToDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public static User convertToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
}
