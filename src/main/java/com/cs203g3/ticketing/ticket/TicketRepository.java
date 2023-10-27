package com.cs203g3.ticketing.ticket;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.user.User;


public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findAllByConcertSessionIdAndSeatSectionIdAndReceiptIsNullOrderBySeatSeatRowAscSeatSeatNumberAsc(Long sessionId, Long sectionId, Pageable pageable);
    List<Ticket> findByConcertSessionConcertAndConcertSession(Concert concert, ConcertSession session);
    List<Ticket> findByReceipt(Receipt receipt);
    List<Ticket> findByReceiptUser(User user);

    Integer countByConcertSessionIdAndReceiptIsNull(Long concertSessionId);
}
