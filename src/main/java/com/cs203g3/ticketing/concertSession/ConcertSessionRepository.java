package com.cs203g3.ticketing.concertSession;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cs203g3.ticketing.concert.Concert;

public interface ConcertSessionRepository extends JpaRepository<ConcertSession, Long> {
    List<ConcertSession> findByConcert(Concert concert);
}
