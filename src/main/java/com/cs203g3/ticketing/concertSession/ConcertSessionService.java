package com.cs203g3.ticketing.concertSession;

import java.util.List;

public interface ConcertSessionService {
    List<ConcertSession> getAllConcertSessionsByConcertId(Long id);

    List<ConcertSession> getAllConcertSessions();
    ConcertSession getConcertSession(Long id);
    ConcertSession addConcertSession(Long concertId, ConcertSession newConcertSession);
    ConcertSession updateConcertSession(Long id, ConcertSession newConcertSession);
    void deleteConcertSession(Long id);
}
