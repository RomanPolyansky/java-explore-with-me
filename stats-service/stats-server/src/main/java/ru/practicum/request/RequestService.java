package ru.practicum.request;

import org.springframework.lang.Nullable;
import ru.practicum.request.entity.Request;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {
    Request saveRequest(Request request);

    List<RequestStatDto> getRequests(LocalDateTime start, LocalDateTime end, List<String> uris, String unique);
}
