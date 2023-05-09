package ru.practicum.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.event.model.EventResponseShortDto;
import ru.practicum.user.model.UserDto;
import java.time.LocalDateTime;

@Getter
@Setter
public class CommentFullResponseDto {
    private Long id;
    private String text;
    private UserDto author;
    private EventResponseShortDto event;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedOn;
}
