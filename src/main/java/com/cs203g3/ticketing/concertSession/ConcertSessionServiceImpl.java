package com.cs203g3.ticketing.concertSession;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cs203g3.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;

@Service
public class ConcertSessionServiceImpl implements ConcertSessionService {
    
    private ConcertSessionRepository concertSessions;
    private ConcertRepository concerts;

    public ConcertSessionServiceImpl(ConcertSessionRepository concertSessions, ConcertRepository concerts) {
        this.concertSessions = concertSessions;
        this.concerts = concerts;
    }

    @Override
    public List<ConcertSession> getAllConcertSessionsByConcertId(Long concertId) {
        return concerts.findById(concertId).map(concert -> {
            return concertSessions.findByConcert(concert);
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
    }

    @Override
    public List<ConcertSession> getAllConcertSessions() {
        return concertSessions.findAll();
    }

    @Override
    public ConcertSession getConcertSession(Long id) {
        return concertSessions.findById(id)
            .map(session -> session)
            .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, id));
    }

    @Override
    public ConcertSession addConcertSession(Long concertId, ConcertSession newConcertSession) {
        concerts.findById(concertId).map(concert -> {
            newConcertSession.setConcert(concert);
            return concert;
        }).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertId));

        return concertSessions.save(newConcertSession);
    }

    @Override
    public ConcertSession updateConcertSession(Long id, ConcertSession newConcertSession) {
        return concertSessions.findById(id).map(cs -> {
            newConcertSession.setId(id);
            return concertSessions.save(newConcertSession);
        }).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, id));
    }

    @Override
    public void deleteConcertSession(Long id) {
        concertSessions.deleteById(id);
    }
}
