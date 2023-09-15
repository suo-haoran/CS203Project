package com.cs203g3.ticketing.concertSession;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ConcertSessionService {

    private ConcertSessionRepository concertSessions;
    private ConcertRepository concerts;

    public ConcertSessionService(ConcertSessionRepository concertSessions, ConcertRepository concerts) {
        this.concertSessions = concertSessions;
        this.concerts = concerts;
    }

    public List<ConcertSession> getAllConcertSessionsByConcertId(Long concertId) {
        return concerts.findById(concertId).map(concert -> {
            return concertSessions.findByConcert(concert);
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
    }

    public ConcertSession getConcertSessionByConcertIdAndSessionId(Long concertId, Long sessionId) {
        return concerts.findById(concertId).map(concert -> {
            return concertSessions.findByConcertAndId(concert, sessionId).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
    }

    public ConcertSession addConcertSession(Long concertId, ConcertSession newConcertSession) {
        concerts.findById(concertId).map(concert -> {
            newConcertSession.setConcert(concert);
            return concert;
        }).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertId));

        return concertSessions.save(newConcertSession);
    }

    public ConcertSession updateConcertSession(Long concertId, Long sessionId, ConcertSession newConcertSession) {
        return concerts.findById(concertId).map(concert -> {
            return concertSessions.findById(sessionId).map(session -> {
                newConcertSession.setId(sessionId);
                newConcertSession.setConcert(concert);
                return concertSessions.save(newConcertSession);
            }).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
    }

    public void deleteConcertSession(Long concertId, Long sessionId) {
        concertSessions.deleteByConcertIdAndId(concertId, sessionId);
    }
}
