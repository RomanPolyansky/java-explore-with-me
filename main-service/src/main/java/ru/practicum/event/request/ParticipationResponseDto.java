package ru.practicum.event.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParticipationResponseDto {
    private Long id;
    private Long event;
    private Long requester;
    private String status;
    private LocalDateTime created;
}
