package ru.practicum.comment.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CommentRequestDto {
    @NotBlank(message = "'text' should not be blank")
    @Size(min = 3, max = 255, message = "'text' should not be more than 255 symbols")
    private String text;
}

