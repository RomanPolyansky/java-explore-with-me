package ru.practicum.event.request;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.event.EventService;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.constants.ParticipationStatus;
import ru.practicum.event.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.event.request.model.ParticipationRequest;
import ru.practicum.event.request.model.QParticipationRequest;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Event event = eventService.getPublishedEventByIdConflict(eventId);
        event.countRequests();
        boolean isRestricted = (requester.getId() == event.getInitiator().getId() ||
                event.getParticipantLimit() <= event.getConfirmedRequests());
        if (isRestricted) {
            throw new DataIntegrityViolationException(
                    String.format("User id=%s is not allowed to request for event id=%s", userId, eventId));
        }
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
    public List<ParticipationRequest> replyToParticipation(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                           long userId, long eventId) {
        Event event = eventService.getPublishedEventById(eventId);
        User user = userService.getUserById(userId);
        if (event.getInitiator().getId() != userId)
            throw new ObjectNotFoundException(String.format("User id=%s is not initiator of event id=%s", userId, eventId));
        if (!event.getRequestModeration()) {
            return getAnyRequestByIds(eventRequestStatusUpdateRequest.getRequestIds());
        }
        long usersToConfirm = eventRequestStatusUpdateRequest.getRequestIds().size();
        event.countRequests();
        long freeSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        ParticipationStatus participationStatusToSet = ParticipationStatus.valueOf(eventRequestStatusUpdateRequest.getStatus());
        if (participationStatusToSet.equals(ParticipationStatus.CONFIRMED)) {
            if (freeSlots < usersToConfirm) {
                throw new DataIntegrityViolationException(
                        String.format("Event id=%s does not have enough slots for %s users", eventId, usersToConfirm));
            }
        }
        List<ParticipationRequest> participationRequests = getAnyRequestByIds(eventRequestStatusUpdateRequest.getRequestIds());
        for (ParticipationRequest participationRequest : participationRequests) {
            if (participationRequest.getStatus().equals(ParticipationStatus.PENDING.name())) {
                participationRequest.setStatus(participationStatusToSet.name());
            } else {
                throw new DataIntegrityViolationException("Can reply to PENDING request only");
            }
        }
        List<ParticipationRequest> savedRequests = requestRepository.saveAll(participationRequests);
        event = eventService.getPublishedEventById(eventId);
        event.countConfirmedRequests();
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            rejectPendingRequests(eventId);
        }
        return savedRequests;
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
    public List<ParticipationRequest> getParticipationRequestsOfUser(long userId) {
        return jpaQueryFactory.selectFrom(QParticipationRequest.participationRequest)
                .where(QParticipationRequest.participationRequest.requester.id.eq(userId))
                .fetch();
    }

    @Override
    public List<ParticipationRequest> getRequestsOfUsersEvent(long userId, long eventId) {
        userService.getUserById(userId);
        Event event = eventService.getPublishedEventById(eventId);
        if (event.getInitiator().getId() != userId)
            throw new ObjectNotFoundException(String.format("User id=%s is not initiator of event id=%s", userId, eventId));
        return event.getParticipationRequests();
    }

    private void rejectPendingRequests(long eventId) {
        List<ParticipationRequest> pendingRequests = jpaQueryFactory.selectFrom(QParticipationRequest.participationRequest)
                .where(QParticipationRequest.participationRequest.event.id.eq(eventId))
                .where(QParticipationRequest.participationRequest.status.eq(ParticipationStatus.PENDING.name()))
                .fetch();
        List<ParticipationRequest> rejectedRequests = pendingRequests.stream()
                .peek(request -> request.setStatus(ParticipationStatus.REJECTED.name()))
                .collect(Collectors.toList());
        requestRepository.saveAll(rejectedRequests);
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

    private List<ParticipationRequest> getAnyRequestByIds(List<Long> requestIds) {
        return requestRepository.findAllById(requestIds);
    }
}
