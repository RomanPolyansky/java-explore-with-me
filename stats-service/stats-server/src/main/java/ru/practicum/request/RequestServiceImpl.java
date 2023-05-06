package ru.practicum.request;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.request.entity.Request;
import ru.practicum.request.entity.QRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Request saveRequest(Request request) {
        request.setTimestamp(LocalDateTime.now());
        Request savedRequest = requestRepository.save(request);
        log.info("RequestService SAVED: {} to the Request Repository", savedRequest);
        return savedRequest;
    }

    @Override
    public List<RequestStatDto> getRequests(LocalDateTime start, LocalDateTime end, List<String> uris, String unique) {
        BooleanExpression inTime = QRequest.request.timestamp.between(start, end)
                .or(QRequest.request.timestamp.eq(start))
                .or(QRequest.request.timestamp.eq(end));

        BooleanExpression inUris;
        if (!uris.isEmpty()) {
            inUris = QRequest.request.uri.in(uris);
            if (uris.size() == 1 && uris.get(0).equalsIgnoreCase("/events")) {
                inUris = QRequest.request.uri.contains("/events");
            }
        } else {
            inUris = Expressions.asBoolean(true).isTrue();
        }

        List<Request> requestList = jpaQueryFactory.selectFrom(QRequest.request)
                .where(inUris)
                .where(inTime)
                .orderBy(QRequest.request.app.desc())
                .fetch();

        Map<RequestStatDto, Long> requestsPerParamMap = new HashMap<>();
        Set<String> distinctIps = new HashSet<>();
        for (Request request : requestList) {
            RequestStatDto requestStatDto = new RequestStatDto(request.getApp(), request.getUri());
            long val = requestsPerParamMap.getOrDefault(requestStatDto, 0L);
            if (unique.equalsIgnoreCase("true") && distinctIps.contains(request.getIp())) { // count distinct
                continue;
            } else {
                distinctIps.add(request.getIp());
            }
            requestsPerParamMap.put(requestStatDto, val + 1L);
        }

        List<RequestStatDto> requestStatDtoList = new ArrayList<>();
        for (RequestStatDto requestStatDto : requestsPerParamMap.keySet()) {
            requestStatDto.setHits(requestsPerParamMap.get(requestStatDto) + 1);
            requestStatDtoList.add(requestStatDto);
        }

//        for (RequestStatDto request : requestStatDtoList) {
//            Request newRequest = new Request();
//            newRequest.setUri(request.getUri());
//            newRequest.setTimestamp(LocalDateTime.now());
//            newRequest.setApp(request.getApp());
//            requestRepository.save(newRequest);
//        }

        log.info("RequestService GET: {} from the Request Repository. From {}. To {}", requestStatDtoList, start, end);
        Collections.sort(requestStatDtoList);
        return requestStatDtoList.stream()
                .filter(requestStatDto -> requestStatDto.getUri().contains("/events/"))
                .collect(Collectors.toList());
    }
}
