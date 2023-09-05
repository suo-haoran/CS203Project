package com.cs203g3.ticketing.venue;

import java.util.List;

import com.cs203g3.ticketing.concert.Concert;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class Venue {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max=255)
    private String name;

    @NotNull
    private int capacity;

    @JsonIgnore
    @OneToMany(mappedBy="venue", cascade = CascadeType.ALL)
    private List<Concert> concert;
}