package ru.practicum.compilation.model;

import org.modelmapper.ModelMapper;

public class CompilationMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static CompilationDto convertToDto(Compilation compilation) {
        return modelMapper.map(compilation, CompilationDto.class);
    }

    public static Compilation convertToEntity(CompilationDto compilationDto) {
        return modelMapper.map(compilationDto, Compilation.class);
    }
}
