package ru.practicum.request;


import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.RequestDto;

@RestController
@RequestMapping
@AllArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/hit")
    public RequestDto saveRequest(@RequestBody RequestDto requestDto) {
        return null;
    }
}
