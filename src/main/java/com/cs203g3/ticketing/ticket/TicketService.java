package com.cs203g3.ticketing.ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.concertSession.ConcertSessionRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.ReceiptRepository;
import com.cs203g3.ticketing.seat.Seat;
import com.cs203g3.ticketing.seat.SeatRepository;
import com.cs203g3.ticketing.ticket.dto.TicketRequestDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseWithoutReceiptDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseWithoutSessionDto;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

@Service
public class TicketService {

    private ModelMapper modelMapper;

    private TicketRepository tickets;
    private ConcertRepository concerts;
    private ConcertSessionRepository sessions;
    private ReceiptRepository receipts;
    private SeatRepository seats;
    private UserRepository users;

    public TicketService(ModelMapper modelMapper, TicketRepository tickets, ConcertRepository concerts,
            ConcertSessionRepository sessions, ReceiptRepository receipts, SeatRepository seats, UserRepository users) {
        this.modelMapper = modelMapper;

        this.tickets = tickets;
        this.concerts = concerts;
        this.sessions = sessions;
        this.receipts = receipts;
        this.seats = seats;
        this.users = users;
    }

    /**
     * Retrieves a list of tickets associated with a specific user identified by
     * their username.
     *
     * @param username The username of the user whose tickets are to be retrieved.
     * @return A list of tickets as TicketResponseWithoutReceiptDto objects.
     * @throws ResourceNotFoundException if the user does not exist.
     */
    public List<TicketResponseWithoutReceiptDto> getAllTicketsByUser(String username) {
        User user = users.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with username %s does not exist", username)));
        return tickets.findByReceiptUser(user).stream()
                .map(ticket -> modelMapper.map(ticket, TicketResponseWithoutReceiptDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of tickets associated with a specific concert session.
     *
     * @param concertId The ID of the concert.
     * @param sessionId The ID of the concert session.
     * @return A list of tickets as TicketResponseWithoutSessionDto objects.
     * @throws ResourceNotFoundException if the concert or session does not exist.
     */
    public List<TicketResponseWithoutSessionDto> getAllTicketsByConcertIdAndSessionId(Long concertId, Long sessionId) {
        Concert concert = concerts.findById(concertId)
                .orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        ConcertSession session = sessions.findByConcertAndId(concert, sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));

        return tickets.findByConcertSessionConcertAndConcertSession(concert, session).stream()
                .map(ticket -> modelMapper.map(ticket, TicketResponseWithoutSessionDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of tickets associated with a specific receipt identified by
     * its UUID.
     *
     * @param receiptId The UUID of the receipt.
     * @return A list of tickets as TicketResponseWithoutReceiptDto objects.
     * @throws ResourceNotFoundException if the receipt does not exist.
     */
    public List<TicketResponseWithoutReceiptDto> getAllTicketsByReceiptId(UUID receiptId) {
        Receipt receipt = receipts.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException(Receipt.class, receiptId));

        return tickets.findByReceipt(receipt).stream()
                .map(ticket -> modelMapper.map(ticket, TicketResponseWithoutReceiptDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a ticket based on its UUID.
     *
     * @param uuid The UUID of the ticket to retrieve.
     * @return A TicketResponseDto representing the ticket.
     * @throws ResourceNotFoundException if the ticket does not exist.
     */
    public TicketResponseDto getTicketByUuid(UUID uuid) {
        return tickets.findById(uuid)
                .map(ticket -> modelMapper.map(ticket, TicketResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException(Ticket.class, uuid));
    }

    /**
     * Creates and adds a new ticket for a specific concert session and seat.
     *
     * @param newTicketDto The data for the new ticket.
     * @return A TicketResponseDto representing the newly created ticket.
     * @throws ResourceNotFoundException if the specified concert session or seat
     *                                   does not exist.
     */
    public TicketResponseDto addTicket(TicketRequestDto newTicketDto) {
        Long sessionId = newTicketDto.getConcertSessionId();
        Long seatId = newTicketDto.getSeatId();

        ConcertSession session = sessions.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, sessionId));
        Seat seat = seats.findBySectionCategoryVenueAndId(session.getConcert().getVenue(), seatId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(
                        "Seat with ID #%d either does not exist or does not belong to Venue of Concert Session with ID #%d",
                        seatId, sessionId)));

        Ticket newTicket = new Ticket(seat, session);
        newTicket = tickets.save(newTicket);
        return modelMapper.map(newTicket, TicketResponseDto.class);
    }

    /**
     * Generates tickets for all seats in a concert session, effectively creating
     * one ticket for each seat.
     * 
     * @param session The concert session for which tickets are to be generated.
     * @return The number of tickets generated.
     */
    public Integer generateTicketsForSession(ConcertSession session) {
        List<Ticket> ticketsToGenerate = new ArrayList<>();

        List<Seat> venueSeats = seats.findAllBySectionCategoryVenue(session.getConcert().getVenue());
        venueSeats.forEach(seat -> {
            ticketsToGenerate.add(new Ticket(seat, session));
        });

        return tickets.saveAll(ticketsToGenerate).size();
    }
}
