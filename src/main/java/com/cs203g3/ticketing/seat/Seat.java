package com.cs203g3.ticketing.seat;

import com.cs203g3.ticketing.section.Section;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueSectionSeatIdentifier", columnNames = { "sectionId", "seatRow", "seatNumber" })
})
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=1, max=5, message="Length of seatRow must be between 1 and 5")
    private String seatRow;

    @NotNull
    private Integer seatNumber;

    @NotNull
    @ManyToOne
    @JoinColumn(name="sectionId")
    private Section section;
}
