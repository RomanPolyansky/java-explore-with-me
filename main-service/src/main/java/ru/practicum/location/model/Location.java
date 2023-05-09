package ru.practicum.location.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "latitude")
    private Float lat;
    @Column(name = "longtitude")
    private Float lon;

    public Location() {
    }
}
