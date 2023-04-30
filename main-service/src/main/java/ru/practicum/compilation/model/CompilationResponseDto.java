package ru.practicum.compilation.model;

import lombok.Data;
import ru.practicum.event.event.model.EventResponseShortDto;

import java.util.List;

@Data
public class CompilationResponseDto {
    private Long id;
    private String title;
    private List<EventResponseShortDto> events;
    private Boolean pinned;
}
