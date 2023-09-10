package com.cs203g3.ticketing.ticket;

import java.util.UUID;

import com.cs203g3.ticketing.concertSession.ConcertSession;

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
    @JoinColumn(name = "categoryId")
    private TicketCategory category;

    @ManyToOne
    @JoinColumn(name = "concertSessionId")
    private ConcertSession concertSession;

    @ManyToOne
    @JoinColumn(name = "receiptId")
    private Receipt receipt;
}
