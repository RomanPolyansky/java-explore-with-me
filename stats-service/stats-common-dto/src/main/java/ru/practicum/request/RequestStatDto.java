package ru.practicum.request;

import lombok.Data;

import java.util.Objects;

@Data
public class RequestStatDto {

    String app;
    String uri;
    Integer hit;

    public RequestStatDto(String app, String uri) {
        this.app = app;
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestStatDto that = (RequestStatDto) o;
        return Objects.equals(app, that.app) && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(app, uri);
    }
}
