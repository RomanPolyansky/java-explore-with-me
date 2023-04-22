package ru.practicum.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RequestDto {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

    public RequestDto() {
    }
}
