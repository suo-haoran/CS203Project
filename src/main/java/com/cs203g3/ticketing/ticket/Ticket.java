package com.cs203g3.ticketing.ticket;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Ticket {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TicketCategory category;

    @ManyToOne
    @JoinColumn(name = "concert_session_id")
    private ConcertSession concertSession;

    @ManyToOne
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;
}
