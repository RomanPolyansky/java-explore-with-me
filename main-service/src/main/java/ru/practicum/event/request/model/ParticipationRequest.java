package ru.practicum.event.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.event.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_requests")
@AllArgsConstructor
@Getter
@Setter
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="event_id", nullable=false)
    private Event event;
    @ManyToOne
    @JoinColumn(name="requester_id", nullable=false)
    private User requester;
    @Column(name = "status")
    private String status;
    @Column(name = "timestamp")
    private LocalDateTime created;

    public ParticipationRequest() {
    }

    public ParticipationRequest(long userId, long eventId) {
        this.event = new Event(eventId);
        this.requester = new User(userId);
        this.created = LocalDateTime.now();
    }
}
