package com.cs203g3.ticketing.concert;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.cs203g3.ticketing.categoryPrice.CategoryPrice;
import com.cs203g3.ticketing.concertImage.ConcertImage;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.venue.Venue;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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

    @NotNull(message="Title must not be null")
    @Size(min=1, max=255, message="Length of title must be between 1 and 255")
    private String title;

    @NotNull(message="Description must not be null")
    @Size(min=1, message="Description must not be blank")
    @Column(columnDefinition="TEXT")
    private String description;

    @NotNull(message="Artist must not be null")
    @Size(min=1, max=255, message="Length of artist must be between 1 and 255")
    private String artist;

    @NotNull(message="Venue must not be null")
    @ManyToOne
    @JoinColumn(name="venueId")
    private Venue venue;

    @JsonIgnore
    @OneToMany(mappedBy="concert", cascade=CascadeType.ALL)
    private List<ConcertSession> sessions;

    @JsonIgnore
    @OneToMany(mappedBy="concert", cascade=CascadeType.ALL)
    private List<CategoryPrice> categoryPrices;

    @OneToMany(mappedBy="concert", cascade=CascadeType.ALL)
    private List<ConcertImage> concertImages;

    @JsonIgnore
    public ConcertSession getEarliestSession() {
        return this.sessions.isEmpty() ? null
            : Collections.min(this.sessions, Comparator.comparing(s -> s.getDatetime()));
    }

    @JsonIgnore
    public ConcertSession getLatestSession() {
        return this.sessions.isEmpty() ? null
            : Collections.max(this.sessions, Comparator.comparing(s -> s.getDatetime()));
    }
}
