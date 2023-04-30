package ru.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.request.RequestDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsClient {

    private final String serverUrl;
    private final HttpClient client;
    private final String app;
    private final HttpResponse.BodyHandler<String> handler;
    private final ObjectMapper jsonMapper;

    public StatsClient(String serverUrl, String app) {
        this.app = app;
        this.serverUrl = serverUrl;
        this.jsonMapper = new ObjectMapper();
        client = HttpClient.newHttpClient();
        handler = HttpResponse.BodyHandlers.ofString();
    }

    public HttpResponse<String> addRequest(String uri, String ip) throws IOException, InterruptedException {
        RequestDto body = RequestDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now().toString())
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonMapper.writeValueAsString(body)))
                .uri(URI.create(serverUrl + "/hit"))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        return client.send(request, handler);
    }

    public HttpResponse<String> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) throws IOException, InterruptedException {
        StringBuilder pathBuilder = new StringBuilder(serverUrl + "/stats");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss");
        pathBuilder.append("?start=").append(start.format(formatter));
        pathBuilder.append("&end=").append(end.format(formatter));

        if (unique) pathBuilder.append("&unique=true");

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                pathBuilder.append("&uris=").append(uri);
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(pathBuilder.toString()))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        return client.send(request, handler);
    }

    public HttpResponse<String> getStatistics(List<String> uris, boolean unique) throws IOException, InterruptedException {
        StringBuilder pathBuilder = new StringBuilder(serverUrl + "/stats");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss");
        pathBuilder.append("?start=").append(LocalDateTime.now().minusYears(100).format(formatter));
        pathBuilder.append("&end=").append(LocalDateTime.now().plusYears(1).format(formatter));

        if (unique) pathBuilder.append("&unique=true");

        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                pathBuilder.append("&uris=").append(uri);
            }
        }

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(pathBuilder.toString()))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json")
                .build();

        return client.send(request, handler);
    }
}
