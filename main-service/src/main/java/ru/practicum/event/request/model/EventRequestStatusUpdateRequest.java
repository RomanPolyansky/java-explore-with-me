package ru.practicum.event.request.model;

import lombok.Data;
import ru.practicum.constraint.validator.ParticipationReqStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "'requestIds' should not be null")
    private List<Long> requestIds;
    @NotBlank
    @ParticipationReqStatus(message = "'status' should CONFIRMED or REJECTED")
    private String status;
}
