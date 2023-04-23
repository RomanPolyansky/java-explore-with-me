package ru.practicum.event.model;

import lombok.Data;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
public class EventResponseUpdateDto {
    private String title;
    private String annotation;
    private String description;
    private Integer participantLimit;
    private Boolean requestModeration = true;
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    private Long confirmedRequests;
    private LocalDateTime createdOn;
    private User initiator;
    private LocalDateTime publishedOn;
    private StateAction stateAction;
    private Long views;
    private Long category;
}
