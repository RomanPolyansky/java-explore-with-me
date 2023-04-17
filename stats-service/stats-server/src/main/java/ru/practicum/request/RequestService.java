package ru.practicum.request;

import org.springframework.lang.Nullable;
import ru.practicum.request.entity.Request;

import java.util.List;

public interface RequestService {
    Request saveRequest(Request request);

    List<RequestStatDto> getRequests(String start, String end, @Nullable List<String> uris, @Nullable String unique);
}
