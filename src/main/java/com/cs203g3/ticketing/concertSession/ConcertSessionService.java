package com.cs203g3.ticketing.concertSession;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.concertSession.dto.ConcertSessionRequestDto;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.ticket.TicketService;

import jakarta.transaction.Transactional;

@Service
public class ConcertSessionService {

    private ModelMapper modelMapper;

    private ConcertSessionRepository concertSessions;
    private ConcertRepository concerts;

    private TicketService ticketService;

    public ConcertSessionService(ModelMapper modelMapper, ConcertSessionRepository concertSessions,
            ConcertRepository concerts, TicketService ticketService) {
        this.modelMapper = modelMapper;

        this.concertSessions = concertSessions;
        this.concerts = concerts;

        this.ticketService = ticketService;
    }

    /**
     * Retrieves a list of concert sessions associated with a specific concert.
     *
     * @param concertId The ID of the concert for which to retrieve sessions.
     * @return A list of ConcertSession objects representing the sessions of the
     *         specified concert.
     * @throws ResourceNotFoundException If the specified concert does not exist.
     */
    public List<ConcertSession> getAllConcertSessionsByConcertId(Long concertId) {
        return concerts.findById(concertId).map(concert -> {
            return concertSessions.findByConcert(concert);
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
    }

    /**
     * Retrieves a specific concert session by its ID and the ID of the associated
     * concert.
     *
     * @param concertId The ID of the concert.
     * @param sessionId The ID of the concert session.
     * @return A ConcertSession object representing the requested session.
     * @throws ResourceNotFoundException If the specified concert or session does
     *                                   not exist.
     */
    public ConcertSession getConcertSessionByConcertIdAndSessionId(Long concertId, Long sessionId) {
        return concerts.findById(concertId).map(concert -> {
            return concertSessions.findByConcertAndId(concert, sessionId)
                    .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
    }

    /**
     * Adds a new concert session to the application and generates tickets for the
     * session.
     *
     * @param concertId            The ID of the concert to associate the session
     *                             with.
     * @param newConcertSessionDto The DTO containing the information of the new
     *                             concert session.
     * @return The newly added ConcertSession object.
     * @throws ResourceNotFoundException If the specified concert does not exist.
     */
    @Transactional
    public ConcertSession addConcertSessionAndGenerateTickets(Long concertId,
            ConcertSessionRequestDto newConcertSessionDto) {
        Concert concert = concerts.findById(concertId)
                .orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));

        ConcertSession newConcertSession = modelMapper.map(newConcertSessionDto, ConcertSession.class);
        newConcertSession.setConcert(concert);
        concertSessions.save(newConcertSession);

        ticketService.generateTicketsForSession(newConcertSession);

        return newConcertSession;
    }

    /**
     * Updates an existing concert session's information.
     *
     * @param concertId            The ID of the concert.
     * @param sessionId            The ID of the session to update.
     * @param newConcertSessionDto The DTO containing the updated information.
     * @return The updated ConcertSession object.
     * @throws ResourceNotFoundException If the specified concert or session does
     *                                   not exist.
     */
    public ConcertSession updateConcertSession(Long concertId, Long sessionId,
            ConcertSessionRequestDto newConcertSessionDto) {
        Concert concert = concerts.findById(concertId)
                .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertId));
        concertSessions.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));

        ConcertSession newConcertSession = modelMapper.map(newConcertSessionDto, ConcertSession.class);
        newConcertSession.setId(sessionId);
        newConcertSession.setConcert(concert);

        return concertSessions.save(newConcertSession);
    }

    /**
     * Deletes a concert session by its ID and the ID of the associated concert.
     *
     * @param concertId The ID of the concert.
     * @param sessionId The ID of the session to delete.
     */
    public void deleteConcertSession(Long concertId, Long sessionId) {
        concertSessions.deleteByConcertIdAndId(concertId, sessionId);
    }
}
