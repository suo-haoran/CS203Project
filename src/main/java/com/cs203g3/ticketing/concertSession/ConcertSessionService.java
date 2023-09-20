package com.cs203g3.ticketing.concertSession;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.concertSession.dto.ConcertSessionRequestDto;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ConcertSessionService {

    @Autowired
    private ModelMapper modelMapper;

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

    public ConcertSession addConcertSession(Long concertId, ConcertSessionRequestDto newConcertSessionDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertId));

        System.out.println(newConcertSessionDto.toString());
        ConcertSession newConcertSession = modelMapper.map(newConcertSessionDto, ConcertSession.class);
        newConcertSession.setConcert(concert);

        return concertSessions.save(newConcertSession);
    }

    public ConcertSession updateConcertSession(Long concertId, Long sessionId, ConcertSessionRequestDto newConcertSessionDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertId));
        concertSessions.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));

        ConcertSession newConcertSession = modelMapper.map(newConcertSessionDto, ConcertSession.class);
        newConcertSession.setId(sessionId);
        newConcertSession.setConcert(concert);

        return concertSessions.save(newConcertSession);
    }

    public void deleteConcertSession(Long concertId, Long sessionId) {
        concertSessions.deleteByConcertIdAndId(concertId, sessionId);
    }
}
