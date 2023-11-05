package com.cs203g3.ticketing.venue;

import java.util.List;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.persistence.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Venue extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="Name must not be null")
    @Column(unique=true)
    @Size(min=3, max=255, message="Name length must be between 3 and 255")
    private String name;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToMany(mappedBy="venue", cascade=CascadeType.ALL)
    private List<Concert> concerts;

    @EqualsAndHashCode.Exclude
    @JsonIgnore
    @OneToMany(mappedBy="venue", cascade=CascadeType.ALL)
    private List<Category> categories;
}