package ru.practicum.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.entity.Request;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping
@Slf4j
public class RequestController {

    private final RequestService requestService;

    @Autowired
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/hit")
    public RequestDto saveRequest(@RequestBody RequestDto requestDto) {
        log.info("POST: /hit with body: {}", requestDto);
        Request request = RequestMapper.convertToEntity(requestDto);
        Request requestSaved = requestService.saveRequest(request);
        return RequestMapper.convertToDto(requestSaved);
    }

    @GetMapping("/stats")
    public List<RequestStatDto> getRequestHistory(
            @RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris",  defaultValue = "") List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") String unique) {
        log.info("GET: /stats with start: {}; end: {}; uris: {}; unique: {}", start, end, uris, unique);
        return requestService.getRequests(start, end, uris, unique);
    }
}