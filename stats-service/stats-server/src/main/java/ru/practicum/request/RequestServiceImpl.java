package ru.practicum.request;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.request.entity.Request;
import ru.practicum.request.entity.QRequest;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Request saveRequest(Request request) {
        request.setTimestamp(LocalDateTime.now());
        return requestRepository.save(request);
    }

    @Override
    public List<RequestStatDto> getRequests(String start, String end, List<String> uris, String unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String startEncoded = URLEncoder.encode(start);
//        String endEncoded = URLEncoder.encode(start);

        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        BooleanExpression inTime = QRequest.request.timestamp.between(startTime, endTime)
                .or(QRequest.request.timestamp.eq(startTime))
                .or(QRequest.request.timestamp.eq(endTime));

        BooleanExpression inUris;

        if (uris != null && !uris.isEmpty()) {
            inUris = QRequest.request.uri.in(uris);
        } else {
            inUris = Expressions.asBoolean(true).isTrue();
        }

        List<Request> requestList = jpaQueryFactory.selectFrom(QRequest.request)
                .where(inUris)
                .where(inTime)
                .orderBy(QRequest.request.app.desc())
                .fetch();

        Map<RequestStatDto, Integer> requestsPerParamMap = new HashMap<>();
        Set<String> distinctIps = new HashSet<>();
        for (Request request : requestList) {
            RequestStatDto requestStatDto = new RequestStatDto(request.getApp(),request.getUri());
            int val = requestsPerParamMap.getOrDefault(requestStatDto, 0);
            if (unique.equalsIgnoreCase("true") && distinctIps.contains(request.getIp())) { // count distinct
                continue;
            } else {
                distinctIps.add(request.getIp());
            }
            requestsPerParamMap.put(requestStatDto, val + 1);
        }

        List<RequestStatDto> requestStatDtoList = new ArrayList<>();
        for (RequestStatDto requestStatDto : requestsPerParamMap.keySet()) {
            requestStatDto.setHit(requestsPerParamMap.get(requestStatDto));
            requestStatDtoList.add(requestStatDto);
        }
        return requestStatDtoList;
    }
}
