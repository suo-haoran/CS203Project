package com.cs203g3.ticketing.concertSession;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/concerts/{concertId}/sessions")
public class ConcertSessionController {
    
    private ConcertSessionService concertSessionService;

    public ConcertSessionController(ConcertSessionService css) {
        this.concertSessionService = css;
    }

    @GetMapping
    public List<ConcertSession> getAllConcertSessionsByConcert(@PathVariable Long concertId) {
        return concertSessionService.getAllConcertSessionsByConcertId(concertId);
    }

    @GetMapping("/{sessionId}")
    public ConcertSession getConcertSession(@PathVariable Long concertId, @PathVariable Long sessionId) {
        return concertSessionService.getConcertSessionByConcertIdAndSessionId(concertId, sessionId);
    }

    @PostMapping
    public ConcertSession addConcertSession(@PathVariable Long concertId, @RequestBody ConcertSession newConcertSession) {
        return concertSessionService.addConcertSession(concertId, newConcertSession);
    }

    @PutMapping("/{sessionId}")
    public ConcertSession updateConcertSession(@PathVariable Long concertId, @PathVariable Long sessionId, @RequestBody ConcertSession newConcertSession) {
        return concertSessionService.updateConcertSession(concertId, sessionId, newConcertSession);
    }

    @DeleteMapping("/{sessionId}")
    public void deleteConcertSession(@PathVariable Long concertId, @PathVariable Long sessionId) {
        concertSessionService.deleteConcertSession(concertId, sessionId);
    }
}
