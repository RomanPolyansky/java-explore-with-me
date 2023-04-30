package ru.practicum.compilation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.event.model.Event;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "compilations")
@AllArgsConstructor
@Getter
@Setter
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "title")
    private String title;

    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    private List<Event> events;

    @Column(name = "pinned")
    private Boolean pinned;


    public Compilation() {
    }

    public Compilation(String title, Boolean pinned) {
        this.title = title;
        this.pinned = pinned;
    }

    public Compilation merge(Compilation other) {
        Compilation compilation = new Compilation();
        if (other.id > 0) compilation.setId(other.getId());
        if (other.title != null) compilation.setTitle(other.getTitle());
        if (other.pinned != null) compilation.setPinned(other.getPinned());
        if (other.events != null) compilation.setEvents(other.getEvents());
        return compilation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compilation category = (Compilation) o;
        return Objects.equals(title, category.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
