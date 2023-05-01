package ru.practicum.event.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.event.event.model.constants.EventState;
import ru.practicum.location.model.Location;

import java.time.LocalDateTime;

@Data
public class EventResponseUpdateDto {
    private String annotation;
    private Long category;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long participantLimit;
    private Boolean requestModeration = true;
    private EventState state;
    private String title;
}