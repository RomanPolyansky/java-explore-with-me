package ru.practicum.comment;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.model.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RestControllerAdvice
@AllArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public CommentShortResponseDto addComment(@PathVariable("userId") long userId,
                                              @PathVariable("eventId") long eventId,
                                              @RequestBody @Validated CommentRequestDto commentRequestDto) {
        Comment comment = CommentMapper.convertToEntity(commentRequestDto);
        log.info("POST /users/{}/events/{}/comments of {}", userId, eventId, comment);
        return CommentMapper.convertToShortDto(commentService.addComment(comment, userId, eventId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{comId}")
    public CommentFullResponseDto changeComment(@RequestBody @Validated CommentRequestDto commentRequestDto,
                                                @PathVariable(value = "userId") long userId,
                                                @PathVariable(value = "eventId") long eventId,
                                                @PathVariable(value = "comId") long comId) {
        Comment comment = CommentMapper.convertToEntity(commentRequestDto);
        log.info("PATCH /users/{}/events/{}/comments of {}", userId, eventId, comment);
        return CommentMapper.convertToFullDto(commentService.changeComment(comment, comId, userId, eventId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/events/{eventId}/comments")
    public List<CommentShortResponseDto> getComments(@PathVariable("eventId") long eventId,
                                                     @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") int from,
                                                     @Positive @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("GET /events/{}/comments from: {}; size: {}", eventId, from, size);
        List<Comment> comments = commentService.getComments(eventId, from, size);
        return comments.stream()
                .map(CommentMapper::convertToShortDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{comId}")
    public void deleteComment(@PathVariable(value = "userId") long userId,
                              @PathVariable(value = "eventId") long eventId,
                              @PathVariable(value = "comId") long comId) {
        log.info("DELETE /users/{}/events/{}/comments/{}", userId, eventId, comId);
        commentService.deleteComment(comId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/comments/{comId}")
    public void deleteComment(@PathVariable(value = "comId") long comId) {
        log.info("DELETE /admin/comments/{}", comId);
        commentService.deleteComment(comId);
    }

}
