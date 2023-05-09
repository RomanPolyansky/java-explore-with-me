package ru.practicum.compilation.model.mapper;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.CompilationDto;
import ru.practicum.event.event.model.Event;

import java.util.Objects;
import java.util.stream.Collectors;

public class CompilationToEntityConverter implements Converter<CompilationDto, Compilation> {
    @Override
    public Compilation convert(MappingContext<CompilationDto, Compilation> mappingContext) {
        CompilationDto source = mappingContext.getSource();
        Compilation destination = mappingContext.getDestination() == null ? new Compilation() : mappingContext.getDestination();

        destination.setEvents(source.getEvents().stream()
                .filter(Objects::nonNull)
                .map(Event::new)
                .collect(Collectors.toList()));
        destination.setTitle(source.getTitle());
        destination.setPinned(source.getPinned());
        return destination;
    }
}
