package ru.practicum.event.event.model;

import lombok.Data;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
public class EventResponseNewDto {
    private String annotation;
    private Long category;
    private String description;
    private LocalDateTime eventDate;
    private Location location;
    private Long participantLimit;
    private Boolean requestModeration = true;
    private Boolean paid;
    private String title;
}
