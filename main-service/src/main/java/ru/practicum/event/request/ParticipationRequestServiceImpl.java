package ru.practicum.event.request;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.event.EventService;
import ru.practicum.event.event.model.Event;
import ru.practicum.event.event.model.constants.ParticipationStatus;
import ru.practicum.event.request.model.*;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
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
    public ParticipationRequest addParticipationRequest(ParticipationRequest newEventRequest) {
        long userId = newEventRequest.getRequester().getId();
        long eventId = newEventRequest.getEvent().getId();
        Optional<ParticipationRequest> participationRequestInRepo = Optional.ofNullable(getAnyRequestByIds(userId, eventId));
        if (participationRequestInRepo.isPresent())
            throw new DataIntegrityViolationException("Cannot send the repeated participation request");
        User requester = userService.getUserById(userId);
        Event event = eventService.getPublishedEventByIdConflict(eventId);
        countRequests(event);
        boolean isRestricted = (requester.getId() == event.getInitiator().getId() ||
                event.getParticipantLimit() <= event.getConfirmedRequests());
        if (isRestricted) {
            throw new DataIntegrityViolationException(
                    String.format("User id=%s is not allowed to request for event id=%s", userId, eventId));
        }
        if (event.getRequestModeration()) {
            newEventRequest.setStatus(ParticipationStatus.PENDING.toString());
        } else {
            newEventRequest.setStatus(ParticipationStatus.CONFIRMED.toString());
        }
        ParticipationRequest participationRequest = requestRepository.save(newEventRequest);
        log.info("ParticipationRequestRepository saved: {}", participationRequest);
        return participationRequest;
    }

    @Override
    public EventRequestStatusUpdateResult replyToParticipation(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                               long userId, long eventId) {
        List<ParticipationRequest> repliedRequests;
        ParticipationStatus participationStatusToSet = ParticipationStatus.valueOf(eventRequestStatusUpdateRequest.getStatus());
        Event event = eventService.getPublishedEventById(eventId);
        User user = userService.getUserById(userId);
        if (event.getInitiator().getId() != user.getId()) {
            throw new ObjectNotFoundException(String.format("User id=%s is not initiator of event id=%s", userId, eventId));
        }
        long usersToRespond = eventRequestStatusUpdateRequest.getRequestIds().size();
        event.countConfirmedRequests();
        checkAvailableSlots(participationStatusToSet, event, usersToRespond);
        List<ParticipationRequest> participationRequests = getAnyRequestByIds(eventRequestStatusUpdateRequest.getRequestIds());
        changeStatuses(participationStatusToSet, participationRequests);

        repliedRequests = getAnyRequestByIds(eventRequestStatusUpdateRequest.getRequestIds()).stream()
                .peek(req -> req.setStatus(participationStatusToSet.name()))
                .collect(Collectors.toList());
        requestRepository.saveAll(repliedRequests);

        event = eventService.getPublishedEventById(eventId);
        event.countConfirmedRequests();
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            rejectPendingRequests(eventId);
        }
        return new EventRequestStatusUpdateResult(repliedRequests.stream()
                .map(ParticipationMapper::convertToDto)
                .collect(Collectors.toList()));
    }

    private void changeStatuses(ParticipationStatus participationStatusToSet, List<ParticipationRequest> participationRequests) {
        for (ParticipationRequest participationRequest : participationRequests) {
            if (participationRequest.getStatus().equals(ParticipationStatus.PENDING.name())) {
                participationRequest.setStatus(participationStatusToSet.name());
            } else {
                throw new DataIntegrityViolationException("Can reply to PENDING request only");
            }
        }
    }

    private void checkAvailableSlots(ParticipationStatus participationStatusToSet, Event event, long usersToRespond) {
        long freeSlots = event.getParticipantLimit() - event.getConfirmedRequests();
        if (participationStatusToSet.equals(ParticipationStatus.CONFIRMED)) {
            if (freeSlots < usersToRespond) {
                throw new DataIntegrityViolationException(
                        String.format("Event id=%s does not have enough slots for %s users", event.getId(), usersToRespond));
            }
        }
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

    public Event countRequests(Event event) {
        if (event.getParticipationRequests() == null) return event;
        Predicate<ParticipationRequest> isConfirmed = req -> req.getStatus().equalsIgnoreCase(ParticipationStatus.CONFIRMED.toString());
        Predicate<ParticipationRequest> isPending = req -> req.getStatus().equalsIgnoreCase(ParticipationStatus.PENDING.toString());
        List<ParticipationRequest> filteredPartRequests = event.getParticipationRequests().stream()
                .filter(isConfirmed.or(isPending))
                .collect(Collectors.toList());
        event.setConfirmedRequests((long) filteredPartRequests.size());
        return event;
    }
}
