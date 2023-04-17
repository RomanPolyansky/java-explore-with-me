package ru.practicum.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}
