package ru.practicum.event.request.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.event.model.constants.ParticipationStatus;
import ru.practicum.event.request.ParticipationResponseDto;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EventRequestStatusUpdateResult {
    private List<ParticipationResponseDto> confirmedRequests = new ArrayList<>();
    private List<ParticipationResponseDto> rejectedRequests = new ArrayList<>();

    public EventRequestStatusUpdateResult(List<ParticipationResponseDto> requests) {
        for (ParticipationResponseDto request : requests) {
            if (request.getStatus().equalsIgnoreCase(ParticipationStatus.CONFIRMED.name())) {
                confirmedRequests.add(request);
            } else if (request.getStatus().equalsIgnoreCase(ParticipationStatus.REJECTED.name())) {
                rejectedRequests.add(request);
            }
        }
    }
}
