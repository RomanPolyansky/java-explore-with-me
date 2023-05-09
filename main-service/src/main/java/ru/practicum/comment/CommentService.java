package ru.practicum.comment;

import ru.practicum.comment.model.Comment;

import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment, long userId, long eventId);

    List<Comment> getComments(long eventId, int from, int size);

    void deleteComment(long comId);

    Comment changeComment(Comment comment, long comId, long userId, long eventId);
}
