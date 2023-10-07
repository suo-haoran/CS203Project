package com.cs203g3.ticketing.section;

import java.util.List;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.persistence.BaseEntity;
import com.cs203g3.ticketing.seat.Seat;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=1, max=255, message="Length of name must be between 1 and 255")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy="section", cascade=CascadeType.ALL)
    private List<Seat> seats;
}
