package ru.practicum.event.request;

import ru.practicum.event.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.event.request.model.EventRequestStatusUpdateResult;
import ru.practicum.event.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequest addParticipationRequest(long eventId, long userId);

    ParticipationRequest cancelParticipation(long userId, long requestId);

    List<ParticipationRequest> getParticipationRequestsOfUser(long userId);

    List<ParticipationRequest> getRequestsOfUsersEvent(long userId, long eventId);

    EventRequestStatusUpdateResult replyToParticipation(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest, long userId, long eventId);
}
