package com.cs203g3.ticketing.category;

import java.util.List;

import com.cs203g3.ticketing.section.Section;
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
public class Category {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="Name must not be null")
    @Column(unique=true)
    @Size(min=3, max=255, message="Name length must be between 3 and 255")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name="venueId")
    private Venue venue;

    @JsonIgnore
    @OneToMany(mappedBy="category", cascade=CascadeType.ALL)
    private List<Section> sections;
}
