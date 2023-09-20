package com.cs203g3.ticketing.concertSession;

import java.time.LocalDateTime;
import java.util.List;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.ticket.Ticket;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class ConcertSession {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="Datetime must not be null")
    private LocalDateTime datetime;

    @NotNull(message="Concert should not be null")
    @ManyToOne
    @JoinColumn(name="concertId")
    private Concert concert;

    @JsonIgnore
    @OneToMany(mappedBy="concertSession")
    private List<Ticket> tickets;
}

