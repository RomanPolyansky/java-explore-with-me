package ru.practicum.request;


import lombok.Data;

@Data
public class RequestDto {
    String app;
    String uri;
    String ip;
    String timestamp;
}
