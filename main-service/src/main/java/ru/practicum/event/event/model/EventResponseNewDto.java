package ru.practicum.event.event.model;

import lombok.Data;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
public class EventResponseNewDto {
    private String title;
    private String annotation;
    private String description;
    private Long participantLimit;
    private Boolean requestModeration = true;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long category;
}
