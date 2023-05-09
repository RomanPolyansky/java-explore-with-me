package ru.practicum.category.model;

import org.modelmapper.ModelMapper;

public class CategoryMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static CategoryDto convertToDto(Category category) {
        return modelMapper.map(category, CategoryDto.class);
    }

    public static Category convertToEntity(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }
}
