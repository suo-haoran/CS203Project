package com.cs203g3.ticketing.ticket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.concertSession.ConcertSessionRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.receipt.Receipt;
import com.cs203g3.ticketing.receipt.ReceiptRepository;
import com.cs203g3.ticketing.seat.Seat;
import com.cs203g3.ticketing.seat.SeatRepository;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.ticket.dto.TicketRequestDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseWithoutReceiptDto;
import com.cs203g3.ticketing.ticket.dto.TicketResponseWithoutSessionDto;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.venue.Venue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @InjectMocks
    private TicketService ticketService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private ConcertSessionRepository concertSessionRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private SeatRepository seatRepository;

     private final User USER = new User(
            1L, "testuser1", "", "exam@gmail.com", "12312312", "SG", new Date(), null);

    private final ConcertSession CONCERT_SESSION = new ConcertSession(
            1L, LocalDateTime.now(), new Concert(
                    1L, "testconcert1", "testdescription1", "artist",
                    new Venue(1L, "Singapore National Stadium", new ArrayList<>(),
                            new ArrayList<>()), new ArrayList<>(),
                    new ArrayList<>(), new ArrayList<>(), new ArrayList<>()),
            new ArrayList<>());

    private final Ticket[] TICKETS = new Ticket[] {
            new Ticket(new Seat(1L, "A2", 12,
                    new Section(1L, "Section 1", new Category(), new ArrayList<>())),
                    CONCERT_SESSION)
    };

    private final Receipt INVOICE = new Receipt(
            UUID.randomUUID(),
            USER,
            new BigDecimal(100.0),
            Arrays.stream(TICKETS).toList());

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getAllTicketsByConcertIdAndSessionId_Success() {
        Long concertId = 1L;
        Long sessionId = 1L;

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(CONCERT_SESSION.getConcert()));
        when(concertSessionRepository.findByConcertAndId(CONCERT_SESSION.getConcert(), sessionId)).thenReturn(Optional.of(CONCERT_SESSION));
        when(ticketRepository.findByConcertSessionConcertAndConcertSession(CONCERT_SESSION.getConcert(), CONCERT_SESSION)).thenReturn(Arrays.asList(TICKETS));

        List<TicketResponseWithoutSessionDto> result = ticketService.getAllTicketsByConcertIdAndSessionId(concertId, sessionId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(concertRepository).findById(concertId);
        verify(concertSessionRepository).findByConcertAndId(CONCERT_SESSION.getConcert(), sessionId);
        verify(ticketRepository).findByConcertSessionConcertAndConcertSession(CONCERT_SESSION.getConcert(), CONCERT_SESSION);
    }

    @Test
    public void getAllTicketsByReceiptId_Success() {
        UUID receiptId = UUID.randomUUID();
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.of(INVOICE));

        List<Ticket> mockTickets = Arrays.asList(TICKETS);
        when(ticketRepository.findByReceipt(INVOICE)).thenReturn(mockTickets);

        List<TicketResponseWithoutReceiptDto> result = ticketService.getAllTicketsByReceiptId(receiptId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(receiptRepository).findById(receiptId);
        verify(ticketRepository).findByReceipt(INVOICE);
    }

    @Test
    public void getTicketByUuid_Success() {
        UUID ticketUuid = UUID.randomUUID();
        TicketResponseDto expected = new TicketResponseDto();
        expected.setId(ticketUuid);
        when(ticketRepository.findById(ticketUuid)).thenReturn(Optional.of(TICKETS[0]));
        when(modelMapper.map(TICKETS[0], TicketResponseDto.class)).thenReturn(expected);
        TicketResponseDto result = ticketService.getTicketByUuid(ticketUuid);

        assertNotNull(result);
        assertEquals(ticketUuid, result.getId());
        verify(ticketRepository).findById(ticketUuid);
    }

    @Test
    public void getTicketByUuid_NotFound_Failure() {
        UUID nonExistentUuid = UUID.randomUUID();
        when(ticketRepository.findById(nonExistentUuid)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ticketService.getTicketByUuid(nonExistentUuid));
        verify(ticketRepository).findById(nonExistentUuid);
    }

    @Test
    public void addTicket_Success() {
        Long sessionId = 1L;
        Long seatId = 1L;
        TicketRequestDto ticketRequestDto = new TicketRequestDto(seatId, sessionId);
        Seat mockSeat = new Seat();

        when(concertSessionRepository.findById(sessionId)).thenReturn(Optional.of(CONCERT_SESSION));
        when(seatRepository.findBySectionCategoryVenueAndId(CONCERT_SESSION.getConcert().getVenue(), seatId)).thenReturn(Optional.of(mockSeat));

        Ticket mockTicket = new Ticket(mockSeat, CONCERT_SESSION);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(mockTicket);
        when(modelMapper.map(mockTicket, TicketResponseDto.class)).thenReturn(new TicketResponseDto());

        TicketResponseDto result = ticketService.addTicket(ticketRequestDto);

        assertNotNull(result);
        verify(concertSessionRepository).findById(sessionId);
        verify(seatRepository).findBySectionCategoryVenueAndId(CONCERT_SESSION.getConcert().getVenue(), seatId);
        verify(ticketRepository).save(any(Ticket.class));

    }

    @Test
    public void addTicket_SessionNotFound_Failure() {
        Long sessionId = 1L;
        Long seatId = 1L;
        when(concertSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        TicketRequestDto ticketRequestDto = new TicketRequestDto(seatId, sessionId);
        assertThrows(ResourceNotFoundException.class, () -> ticketService.addTicket(ticketRequestDto));
        verify(concertSessionRepository).findById(sessionId);
    }

    @Test
    public void addTicket_SeatNotFound_Failure() {
        Long sessionId = 1L;
        Long seatId = 1L;
        when(concertSessionRepository.findById(sessionId)).thenReturn(Optional.of(CONCERT_SESSION));
        when(seatRepository.findBySectionCategoryVenueAndId(CONCERT_SESSION.getConcert().getVenue(), seatId)).thenReturn(Optional.empty());

        TicketRequestDto ticketRequestDto = new TicketRequestDto(seatId, sessionId);
        assertThrows(ResourceNotFoundException.class, () -> ticketService.addTicket(ticketRequestDto));
        verify(concertSessionRepository).findById(sessionId);
        verify(seatRepository).findBySectionCategoryVenueAndId(CONCERT_SESSION.getConcert().getVenue(), seatId);
    }

    @Test
    public void generateTicketsForSession_Success() {

        List<Seat> mockSeats = Arrays.asList(new Seat(1L, "A", 1, null), new Seat(2L, "A", 2, null), new Seat(3L, "A", 3, null));

        when(seatRepository.findAllBySectionCategoryVenue(CONCERT_SESSION.getConcert().getVenue())).thenReturn(mockSeats);

        List<Ticket> mockGeneratedTickets = new ArrayList<>();
        for (Seat seat : mockSeats) {
            mockGeneratedTickets.add(new Ticket(seat, CONCERT_SESSION));
        }

        when(ticketRepository.saveAll(any(List.class))).thenReturn(mockGeneratedTickets);

        int generatedTicketCount = ticketService.generateTicketsForSession(CONCERT_SESSION);

        assertEquals(mockSeats.size(), generatedTicketCount);
        verify(seatRepository).findAllBySectionCategoryVenue(CONCERT_SESSION.getConcert().getVenue());
        verify(ticketRepository).saveAll(any(List.class));
    }
}
