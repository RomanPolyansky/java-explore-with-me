package ru.practicum.comment.model;

import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class CommentMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Comment convertToEntity(CommentRequestDto commentRequestDto) {
        return modelMapper.map(commentRequestDto, Comment.class);
    }

    public static CommentShortResponseDto convertToShortDto(Comment comment) {
        return modelMapper.map(comment, CommentShortResponseDto.class);
    }

    public static CommentFullResponseDto convertToFullDto(Comment comment) {
        return modelMapper.map(comment, CommentFullResponseDto.class);
    }
}
