package ru.practicum.event.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.event.request.model.EventRequestStatusUpdateResult;
import ru.practicum.event.request.model.ParticipationMapper;
import ru.practicum.event.request.model.ParticipationRequest;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
@RestControllerAdvice
@RequiredArgsConstructor
public class ParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/requests")
    public ParticipationResponseDto addParticipationRequest(@PathVariable("userId") long userId,
                                                            @RequestParam("eventId") long eventId) {
        log.info("POST /users/{}/requests of event: {}", userId, eventId);
        ParticipationRequest newEventRequest = new ParticipationRequest(userId, eventId);
        return ParticipationMapper.convertToDto(
                participationRequestService.addParticipationRequest(newEventRequest));
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult replyToParticipation(
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
            @PathVariable(value = "userId") long userId,
            @PathVariable(value = "eventId") long eventId) {
        log.info("PATCH /users/{}/events/{}/requests", userId, eventId);
        return participationRequestService.replyToParticipation(eventRequestStatusUpdateRequest, userId, eventId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationResponseDto cancelParticipation(
            @PathVariable(value = "userId") long userId,
            @PathVariable(value = "requestId") long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return ParticipationMapper.convertToDto(
                participationRequestService.cancelParticipation(userId, requestId));
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/requests")
    public List<ParticipationResponseDto> getRequestsOfUser(@PathVariable("userId") long userId) {
        log.info("GET /users/{}/requests", userId);
        return participationRequestService.getParticipationRequestsOfUser(userId).stream()
                .map(ParticipationMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationResponseDto> getRequestsOfUsersEvent(@PathVariable("userId") long userId,
                                                                  @PathVariable("eventId") long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return participationRequestService.getRequestsOfUsersEvent(userId, eventId).stream()
                .map(ParticipationMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
