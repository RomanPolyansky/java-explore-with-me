package ru.practicum.event.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.request.model.ParticipationRequest;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "events")
@AllArgsConstructor
@Getter
@Setter
public class Event implements Comparable<Event> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "annotation")
    private String annotation;
    @Column(name = "description")
    private String description;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "participants_limit")
    private Long participantLimit;
    @Column(name = "request_moderation")
    private Boolean requestModeration;
    @Column(name = "event_date")
    private LocalDateTime eventDate;
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name="location_id", nullable=false)
    private Location location;
    @Column(name = "created_on")
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name="category_id", nullable=false)
    private Category category;
    @ManyToOne
    @JoinColumn(name="initiator_id", nullable=false)
    private User initiator;
    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            inverseJoinColumns = @JoinColumn(name = "event_id"),
            joinColumns = @JoinColumn(name = "compilation_id"))
    private List<Compilation> compilation;
    @Column(name = "status")
    private String statusStr;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @OneToMany(mappedBy="event")
    private List<ParticipationRequest> participationRequests;
    @Transient
    private StateAction stateAction;
    @Transient
    private Long confirmedRequests;
    @Transient
    private long views;

    public Event(long eventId) {
        this.id = eventId;
    }

    @PostLoad
    private void setState(){
        stateAction = StateAction.valueOf(statusStr);
    }

    public Event countConfirmedRequests() {
        List<ParticipationRequest> filteredPartRequests = participationRequests.stream()
                .filter(req -> req.getStatus().equals(ParticipationStatus.CONFIRMED.toString()))
                .collect(Collectors.toList());
        confirmedRequests = (long) filteredPartRequests.size();
        return this;
    }

    public Event() {
    }

    public Event merge(Event other) {
        if(other.title != null) title = other.title;
        if(other.annotation != null) annotation = other.annotation;
        if(other.description != null) description = other.description;
        if(other.paid != null) paid = other.paid;
        if(other.participantLimit != null) participantLimit = other.participantLimit;
        if(other.requestModeration != null) requestModeration = other.requestModeration;
        if(other.eventDate != null) eventDate = other.eventDate;
        if(other.location != null) location = other.location;
        if(other.createdOn != null) createdOn = other.createdOn;
        if(other.category != null) category = other.category;
        if(other.initiator != null) initiator = other.initiator;
        if(other.statusStr != null) statusStr = other.statusStr;
        if(other.publishedOn != null) publishedOn = other.publishedOn;
        if(other.stateAction != null) stateAction = other.stateAction;
        if (stateAction != null) statusStr = stateAction.name();
        return this;
    }

    @Override
    public int compareTo(Event o) {
        return (int) (this.views - o.views);
    }
}
