package com.cs203g3.ticketing.ticket;

import java.util.UUID;

import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.persistence.BaseEntity;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.seat.Seat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueSessionAndSeat", columnNames = { "concertSessionId", "seatId" })
})
@EqualsAndHashCode(callSuper = true)
public class Ticket extends BaseEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "seatId")
    private Seat seat;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concertSessionId")
    private ConcertSession concertSession;

    @ManyToOne
    @JoinColumn(name = "receiptId")
    private Receipt receipt;

    public Ticket(Seat seat, ConcertSession session) {
        this.seat = seat;
        this.concertSession = session;
    }
}
