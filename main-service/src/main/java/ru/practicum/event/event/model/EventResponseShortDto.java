package ru.practicum.event.event.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Data
public class EventResponseShortDto {
    private Long id;
    private String title;
    private String annotation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Category category;
    private Boolean paid;
    private Long confirmedRequests;
    private User initiator;
    private long views;
}
