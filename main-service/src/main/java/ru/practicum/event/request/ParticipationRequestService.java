package ru.practicum.event.request;

import ru.practicum.event.request.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequest addParticipationRequest(long eventId, long userId);

    ParticipationRequest cancelParticipation(long userId, long requestId);

    List<ParticipationRequest> getParticipationRequests(long userId);
}
