package ru.practicum.event.request;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.event.EventService;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.ParticipationStatus;
import ru.practicum.event.request.model.ParticipationRequest;
import ru.practicum.event.request.model.QParticipationRequest;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final UserService userService;
    private final EventService eventService;
    private final ParticipationRequestRepository requestRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ParticipationRequest addParticipationRequest(long eventId, long userId) {
        Optional<ParticipationRequest> participationRequestInRepo = Optional.ofNullable(getAnyRequestByIds(userId, eventId));
        if (participationRequestInRepo.isPresent())
            throw new DataIntegrityViolationException("Cannot send the repeated participation request");

        User requester = userService.getUserById(userId);
        ParticipationRequest newEventRequest = new ParticipationRequest(userId, eventId);
        Event event = eventService.getPublishedEventById(eventId);
//        boolean isRestricted = requester.getId() == event.getInitiator().getId() ||
//                event.getParticipantLimit() <= event.getConfirmedRequests();
//        if (isRestricted) {
//            throw new DataIntegrityViolationException(
//                    String.format("User id=%s is not allowed to request for event id=%s", userId, eventId));
//        }
        if (event.getRequestModeration())  {
            newEventRequest.setStatus(ParticipationStatus.PENDING.toString());
        } else {
            newEventRequest.setStatus(ParticipationStatus.CONFIRMED.toString());
        }
        ParticipationRequest participationRequest = requestRepository.save(newEventRequest);
        log.info("ParticipationRequestRepository saved: {}", participationRequest);
        return participationRequest;
    }

    @Override
    public ParticipationRequest cancelParticipation(long userId, long requestId) {
        ParticipationRequest requestInRepo = getAnyRequestByIds(requestId);
        userService.getUserById(userId);
        if (requestInRepo.getRequester().getId() != userId) {
            throw new DataIntegrityViolationException(
                    String.format("User id=%s is not requester of participation request id=%s", userId, requestId));
        }
        requestInRepo.setStatus(ParticipationStatus.CANCELED.name());
        ParticipationRequest savedParticipationRequest = requestRepository.save(requestInRepo);
        log.info("ParticipationRequestRepository changed to: {}", savedParticipationRequest);
        return savedParticipationRequest;
    }

    @Override
    public List<ParticipationRequest> getParticipationRequests(long userId) {
        return jpaQueryFactory.selectFrom(QParticipationRequest.participationRequest)
                .where(QParticipationRequest.participationRequest.requester.id.eq(userId))
                .fetch();
    }

    private ParticipationRequest getAnyRequestByIds(long userId, long eventId) {
        return jpaQueryFactory.selectFrom(QParticipationRequest.participationRequest)
                .where(QParticipationRequest.participationRequest.requester.id.eq(userId))
                .where(QParticipationRequest.participationRequest.event.id.eq(eventId))
                .fetchOne();
    }

    private ParticipationRequest getAnyRequestByIds(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("ParticipationRequest with id=%s is not found", requestId)));
    }
}
