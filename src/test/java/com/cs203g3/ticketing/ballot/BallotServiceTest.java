package com.cs203g3.ticketing.ballot;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cs203g3.ticketing.activeBallotCategory.EnumActiveBallotCategoryStatus;
import com.cs203g3.ticketing.ballot.dto.BallotResponseDto;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.concertSession.ConcertSessionRepository;
import com.cs203g3.ticketing.email.EmailService;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;
import com.cs203g3.ticketing.venue.Venue;

@ExtendWith(MockitoExtension.class)
public class BallotServiceTest {

    @InjectMocks
    private BallotService ballotService;

    @Mock
    private BallotRepository ballots;

    @Mock
    private CategoryRepository categories;

    @Mock
    private ConcertSessionRepository concertSessions;

    @Mock
    private UserRepository users;

    @Mock
    private ModelMapper modelMapper;

    @Mock 
    private EmailService emailService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void verifyValidConcertSessionIdAndCategoryId_Success() {
        Long concertSessionId = 1L;
        Long categoryId = 2L;

        Venue venue = new Venue();
        Concert concert = new Concert();
        concert.setVenue(venue);
        ConcertSession concertSession = new ConcertSession();
        concertSession.setConcert(concert);

        // Mock the behavior of repositories
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.of(concertSession));
        when(categories.findByVenueAndId(venue, categoryId)).thenReturn(Optional.of(new Category()));

        ballotService.verifyValidConcertSessionIdAndCategoryId(concertSessionId, categoryId);

        // Assertions
        verify(concertSessions).findById(concertSessionId);
        verify(categories).findByVenueAndId(venue, categoryId);
    }

    @Test
    public void verifyValidConcertSessionIdAndCategoryId_ConcertSessionDoesNotExist_Failure() {
        Long concertSessionId = 1L;
        Long categoryId = 2L;

        // Mock the behavior of repositories
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.verifyValidConcertSessionIdAndCategoryId(concertSessionId, categoryId);
        });

        // Assertions
        verify(concertSessions).findById(concertSessionId);
    }

    @Test
    public void verifyValidConcertSessionIdAndCategoryId_CategoryDoesNotExist_Failure() {
        Long concertSessionId = 1L;
        Long categoryId = 2L;

        Venue venue = new Venue();
        Concert concert = new Concert();
        concert.setVenue(venue);
        ConcertSession concertSession = new ConcertSession();
        concertSession.setConcert(concert);

        // Mock the behavior of repositories
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.of(concertSession));
        when(categories.findByVenueAndId(venue, categoryId)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.verifyValidConcertSessionIdAndCategoryId(concertSessionId, categoryId);
        });

        // Assertions
        verify(concertSessions).findById(concertSessionId);
        verify(categories).findByVenueAndId(venue, categoryId);
    }

    @Test
    public void getAllBallotsByConcertSessionIdAndCategoryId_Success() {
        Long concertSessionId = 1L;
        Long categoryId = 2L;
        Ballot ballot = new Ballot();
        ballot.setId(1L);
        BallotResponseDto ballotDto = new BallotResponseDto();

        Venue venue = new Venue();
        Concert concert = new Concert();
        concert.setVenue(venue);
        ConcertSession concertSession = new ConcertSession();
        concertSession.setConcert(concert);

        // Mock the behavior of repositories
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.of(concertSession));
        when(categories.findByVenueAndId(venue, categoryId)).thenReturn(Optional.of(new Category()));
        when(ballots.findAllByConcertSessionIdAndCategoryId(concertSessionId, categoryId))
                .thenReturn(Collections.singletonList(ballot));
        when(modelMapper.map(any(Ballot.class), eq(BallotResponseDto.class))).thenReturn(ballotDto);

        List<BallotResponseDto> result = ballotService.getAllBallotsByConcertSessionIdAndCategoryId(concertSessionId, categoryId);

        // Assertions
        assertTrue(result.size() == 1);
        assertTrue(result.get(0) == ballotDto);
        verify(concertSessions).findById(concertSessionId);
        verify(categories).findByVenueAndId(venue, categoryId);
        verify(ballots).findAllByConcertSessionIdAndCategoryId(concertSessionId, categoryId);
        verify(modelMapper).map(any(Ballot.class), eq(BallotResponseDto.class));
    }

    @Test
    public void addBallot_Success() {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(1L);
        Long concertSessionId = 1L;
        Long categoryId = 2L;
        User user = new User();
        user.setId(1L);

        Concert concert = new Concert();
        ConcertSession concertSession = new ConcertSession();
        concertSession.setConcert(concert);

        Category category = new Category();
        BallotResponseDto ballotDto = new BallotResponseDto();

        // Mock the behavior of repositories
        when(users.findById(userDetails.getId())).thenReturn(Optional.of(user));
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.of(concertSession));
        when(categories.findByVenueAndIdAndActiveBallotCategoriesConcertSessionsAndActiveBallotCategoriesStatus(concertSession.getConcert().getVenue(), categoryId,
                concertSession, EnumActiveBallotCategoryStatus.ACTIVE)).thenReturn(Optional.of(category));
        when(ballots.save(Mockito.any(Ballot.class))).thenReturn(new Ballot());
        when(modelMapper.map(any(Ballot.class), eq(BallotResponseDto.class))).thenReturn(ballotDto);
        doNothing().when(emailService).sendBallotingConfirmationEmail(user, concertSession);

        BallotResponseDto result = ballotService.addBallot(userDetails, concertSessionId, categoryId);

        // Assertions
        assertTrue(result != null);

        verify(users).findById(userDetails.getId());
        verify(concertSessions).findById(concertSessionId);
        verify(categories).findByVenueAndIdAndActiveBallotCategoriesConcertSessionsAndActiveBallotCategoriesStatus(concertSession.getConcert().getVenue(), categoryId, concertSession, EnumActiveBallotCategoryStatus.ACTIVE);
        verify(ballots).save(Mockito.any(Ballot.class));
        verify(modelMapper).map(any(Ballot.class), eq(BallotResponseDto.class));
    }

    @Test
    public void addBallot_UserDoesNotExist_Failure() {
        // setup the mock
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(-1L);
        Long concertSessionId = 1L;
        Long categoryId = 2L;

        when(users.findById(userDetails.getId())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.addBallot(userDetails, concertSessionId, categoryId);
        });
        verify(users).findById(userDetails.getId());
    }

    @Test
    public void addBallot_ConcertSessionDoesNotExist_Failure() {
        // setup the mock
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(-1L);
        Long concertSessionId = 1L;
        Long categoryId = 2L;

        when(users.findById(userDetails.getId())).thenReturn(Optional.of(new User()));
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.addBallot(userDetails, concertSessionId, categoryId);
        });
        verify(users).findById(userDetails.getId());
        verify(concertSessions).findById(concertSessionId);
    }

    @Test
    public void addBallot_CategoryDoesNotExist_Failure() {
        // setup the mock
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(-1L);
        Long concertSessionId = 1L;
        Long categoryId = 2L;

        Concert concert = new Concert();
        ConcertSession concertSession = new ConcertSession();
        concertSession.setConcert(concert);

        when(users.findById(userDetails.getId())).thenReturn(Optional.of(new User()));
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.of(concertSession));
        when(categories.findByVenueAndIdAndActiveBallotCategoriesConcertSessionsAndActiveBallotCategoriesStatus(null, categoryId, concertSession, EnumActiveBallotCategoryStatus.ACTIVE))
                .thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.addBallot(userDetails, concertSessionId, categoryId);
        });
        verify(users).findById(userDetails.getId());
        verify(concertSessions).findById(concertSessionId);
    }

    @Test
    public void randomizeBallotForConcertSessionIdAndCategoryId_Success() {
        Long concertSessionId = 1L;
        Long categoryId = 2L;
        Ballot ballot = new Ballot();
        ballot.setId(1L);

        Venue venue = new Venue();
        Concert concert = new Concert();
        concert.setVenue(venue);
        ConcertSession concertSession = new ConcertSession();
        concertSession.setConcert(concert);

        // Mock the behavior of repositories
        when(concertSessions.findById(concertSessionId)).thenReturn(Optional.of(concertSession));
        when(categories.findByVenueAndId(venue, categoryId)).thenReturn(Optional.of(new Category()));
        when(ballots.findAllByConcertSessionIdAndCategoryId(concertSessionId, categoryId))
                .thenReturn(Collections.singletonList(ballot));
        ballotService.randomiseBallotForConcertSessionIdAndCategoryId(concertSessionId, categoryId);

        // Assertions
        verify(ballots).saveAll(Mockito.anyList());
        verify(concertSessions).findById(concertSessionId);
        verify(categories).findByVenueAndId(venue, categoryId);
        verify(ballots).findAllByConcertSessionIdAndCategoryId(concertSessionId, categoryId);
    }
}
