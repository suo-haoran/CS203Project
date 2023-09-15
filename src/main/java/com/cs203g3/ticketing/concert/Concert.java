package com.cs203g3.ticketing.concert;


import java.util.List;

import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.venue.Venue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Concert {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max=255)
    private String title;

    @NotNull
    private String description;

    @NotNull
    @Size(max=255)
    private String artist;

    @NotNull
    @ManyToOne
    @JoinColumn(name="venueId")
    private Venue venue;

    @JsonIgnore
    @OneToMany(mappedBy="concert", cascade=CascadeType.ALL)
    private List<ConcertSession> sessions;
}
