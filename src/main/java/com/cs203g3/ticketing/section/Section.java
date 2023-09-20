package com.cs203g3.ticketing.section;

import com.cs203g3.ticketing.venue.Venue;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=1, max=255, message="Length of name must be between 1 and 255")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name="venueId")
    private Venue venue;
}
