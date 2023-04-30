package ru.practicum.event.event.model;

import lombok.Data;
import ru.practicum.event.event.model.constants.EventState;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
public class EventResponseUpdateDto {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration = true;
    private EventState state;
    private String title;
}