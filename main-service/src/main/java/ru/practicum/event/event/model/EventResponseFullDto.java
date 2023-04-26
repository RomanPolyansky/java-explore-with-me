package ru.practicum.event.event.model;

import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
public class EventResponseFullDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private Integer participantLimit;
    private Boolean requestModeration = true;
    private LocalDateTime eventDate;
    private Location location;
    private Category category;
    private Boolean paid;
    private Long confirmedRequests;
    private LocalDateTime createdOn;
    private User initiator;
    private LocalDateTime publishedOn;
    private StateAction state;
    private long views;
}
