package com.cs203g3.ticketing.concertSession;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cs203g3.ticketing.concert.Concert;

import jakarta.transaction.Transactional;

public interface ConcertSessionRepository extends JpaRepository<ConcertSession, Long> {
    List<ConcertSession> findByConcert(Concert concert);
    Optional<ConcertSession> findByConcertAndId(Concert concert, Long sessionId);

    @Transactional
    void deleteByConcertIdAndId(Long concertId, Long sessionId);
}
