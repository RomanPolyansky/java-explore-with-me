package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.category.model.Category;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Event {

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
    private Integer participantLimit;
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
    @Column(name = "status")
    private String statusStr;
    @Column(name = "status_datetime")
    private LocalDateTime statusDateTime;
    @Transient
    private StateAction stateAction;

    @PostLoad
    private void setState(){
        stateAction = StateAction.valueOf(statusStr);
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
        if(other.statusDateTime != null) statusDateTime = other.statusDateTime;
        if(other.stateAction != null) stateAction = other.stateAction;
        return this;
    }
}
