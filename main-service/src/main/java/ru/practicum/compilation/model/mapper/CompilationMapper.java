package ru.practicum.compilation.model.mapper;

import org.modelmapper.ModelMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.compilation.model.CompilationResponseDto;

public class CompilationMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    static {
        modelMapper.addConverter(new CompilationToEntityConverter());
    }

    public static CompilationResponseDto convertToDto(Compilation compilation) {
        return modelMapper.map(compilation, CompilationResponseDto.class);
    }

    public static Compilation convertToEntity(CompilationDto compilationDto) {
        return modelMapper.map(compilationDto, Compilation.class);
    }
}
