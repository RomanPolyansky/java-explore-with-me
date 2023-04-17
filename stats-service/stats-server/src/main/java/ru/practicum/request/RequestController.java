package ru.practicum.request;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.entity.Request;

import java.util.List;


@RestController
@RequestMapping
@AllArgsConstructor
@Slf4j
public class RequestController {

    private final RequestService requestService;

    private final ModelMapper modelMapper;

    @PostMapping("/hit")
    public RequestDto saveRequest(@RequestBody RequestDto requestDto) {
        log.info("POST: /hit with body: {}", requestDto);
        Request request = convertToEntity(requestDto);
        Request requestSaved = requestService.saveRequest(request);
        return convertToDto(requestSaved);
    }

    @GetMapping("/stats")
    public List<RequestStatDto> getRequestHistory(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris",  defaultValue = "") List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") String unique) {
        log.info("GET: /stats with start: {}; end: {}; uris: {}; unique: {}", start, end, uris, unique);
        return requestService.getRequests(start, end, uris, unique);
    }

    private RequestDto convertToDto(Request request) {
        RequestDto postDto = modelMapper.map(request, RequestDto.class);

//        postDto.setSubmissionDate(post.getSubmissionDate(),
//                userService.getCurrentUser().getPreference().getTimezone());

        return postDto;
    }

    private Request convertToEntity(RequestDto requestDto) {
        Request post = modelMapper.map(requestDto, Request.class);

//        post.setSubmissionDate(requestDto.getSubmissionDateConverted(
//                userService.getCurrentUser().getPreference().getTimezone()));
//
//        if (requestDto.getId() != null) {
//            Post oldPost = postService.getPostById(requestDto.getId());
//            post.setRedditID(oldPost.getRedditID());
//            post.setSent(oldPost.isSent());
//        }

        return post;
    }
}
