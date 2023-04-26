package ru.practicum.event.request.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.request.ParticipationResponseDto;
import ru.practicum.event.request.ParticipationRequestService;

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
        return ParticipationMapper.convertToDto(
                participationRequestService.addParticipationRequest(eventId, userId));
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
        return participationRequestService.getParticipationRequests(userId).stream()
                .map(ParticipationMapper::convertToDto)
                .collect(Collectors.toList());
    }
}
