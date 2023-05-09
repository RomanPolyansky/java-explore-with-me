package ru.practicum.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Data
@Getter
@Setter
public class RequestStatDto implements Comparable<RequestStatDto> {

    String app;
    String uri;
    Long hits;

    public RequestStatDto() {
    }

    public RequestStatDto(String app, String uri) {
        this.app = app;
        this.uri = uri;
    }

    public RequestStatDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
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

    @Override
    public int compareTo(RequestStatDto o) {
        return Long.compare(o.hits, hits);
    }
}
