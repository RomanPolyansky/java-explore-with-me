package ru.practicum.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.model.QComment;
import ru.practicum.event.event.EventService;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.constants.ParticipationStatus;
import ru.practicum.event.request.ParticipationRequestService;
import ru.practicum.event.request.model.ParticipationRequest;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.security.AccessControlException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserService userService;
    private final EventService eventService;
    private final CommentRepository commentRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final ParticipationRequestService participationRequestService;

    @Override
    public Comment addComment(Comment comment, long userId, long eventId) {
        User author = userService.getUserById(userId);
        Event event = eventService.getPublishedEventById(eventId);
        if (!isConfirmedParticipationRequest(eventId, userId))
            throw new DataIntegrityViolationException("User is not confirmed to participate in event");
        comment.setAuthor(author);
        comment.setEvent(event);
        comment.setCreatedOn(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);
        log.info("CommentRepository saved: {}", savedComment);
        return savedComment;
    }

    @Override
    public List<Comment> getComments(long eventId, int from, int size) {
        eventService.getPublishedEventById(eventId);
        List<Comment> foundComments = IterableUtils.toList(jpaQueryFactory.selectFrom(QComment.comment)
                .where(QComment.comment.event.id.eq(eventId))
                .orderBy(QComment.comment.createdOn.desc())
                .offset(from)
                .limit(size)
                .fetch());
        log.info("CommentService found {}", foundComments);
        return foundComments;
    }

    @Override
    public void deleteComment(long comId) {
        Comment commentInRepo = getCommentById(comId);
        log.info("CommentRepository deletes: {}", commentInRepo);
        commentRepository.deleteById(comId);
    }

    @Override
    public Comment changeComment(Comment commentChangeTo, long comId, long userId, long eventId) {
        User author = userService.getUserById(userId);
        eventService.getPublishedEventById(eventId);
        Comment commentInRepo = getCommentById(comId);
        if (commentInRepo.getAuthor().getId() != author.getId()) {
            throw new AccessControlException(
                    String.format("User id=%d is not author of the comment id=%d", userId, comId));
        }
        mergeComments(commentChangeTo, commentInRepo);
        commentChangeTo.setModifiedOn(LocalDateTime.now());
        log.info("CommentRepository had: {}; changing to: {}", commentInRepo, commentChangeTo);
        return commentRepository.save(commentChangeTo);
    }

    private boolean isConfirmedParticipationRequest(long eventId, long userId) {
        List<ParticipationRequest> participationRequestList = participationRequestService.getParticipationRequestsOfUser(userId);
        for (ParticipationRequest pr : participationRequestList) {
            if (pr.getEvent().getId() == eventId)
                if (pr.getStatus().equalsIgnoreCase(ParticipationStatus.CONFIRMED.name()))
                    return true;
        }
        return false;
    }

    private void mergeComments(Comment targetComment, Comment sourceComment) {
        if (targetComment.getText() == null) targetComment.setText(sourceComment.getText());
        if (targetComment.getId() == null) targetComment.setId(sourceComment.getId());
        if (targetComment.getAuthor() == null) targetComment.setAuthor(sourceComment.getAuthor());
        if (targetComment.getEvent() == null) targetComment.setEvent(sourceComment.getEvent());
        if (targetComment.getModifiedOn() == null) targetComment.setModifiedOn(sourceComment.getModifiedOn());
        if (targetComment.getCreatedOn() == null) targetComment.setCreatedOn(sourceComment.getCreatedOn());
    }

    private Comment getCommentById(long comId) {
        return commentRepository.findById(comId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Comment with id %s does not exist", comId))
        );
    }
}
