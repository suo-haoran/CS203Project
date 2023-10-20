package com.cs203g3.ticketing.ballot;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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

import com.cs203g3.ticketing.ballot.dto.BallotResponseDto;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class BallotServiceTest {

    @InjectMocks
    private BallotService ballotService;

    @Mock
    private BallotRepository ballots;

    @Mock
    private CategoryRepository categories;

    @Mock
    private ConcertRepository concerts;

    @Mock
    private UserRepository users;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void verifyValidConcertIdAndCategoryId_Success() {
        Long concertId = 1L;
        Long categoryId = 2L;

        // Mock the behavior of repositories
        when(concerts.findById(concertId)).thenReturn(Optional.of(new Concert()));
        when(categories.findById(categoryId)).thenReturn(Optional.of(new Category()));

        ballotService.verifyValidConcertIdAndCategoryId(concertId, categoryId);

        // Assertions
        verify(concerts).findById(concertId);
        verify(categories).findById(categoryId);
    }

    @Test
    public void verifyValidConcertIdAndCategoryId_ConcertDoesNotExist_Failure() {
        Long concertId = 1L;
        Long categoryId = 2L;

        // Mock the behavior of repositories
        when(concerts.findById(concertId)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.verifyValidConcertIdAndCategoryId(concertId, categoryId);
        });

        // Assertions
        verify(concerts).findById(concertId);
    }

    @Test
    public void verifyValidConcertIdAndCategoryId_CategoryDoesNotExist_Failure() {
        Long concertId = 1L;
        Long categoryId = 2L;

        // Mock the behavior of repositories
        when(concerts.findById(concertId)).thenReturn(Optional.of(new Concert()));
        when(categories.findById(categoryId)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.verifyValidConcertIdAndCategoryId(concertId, categoryId);
        });

        // Assertions
        verify(concerts).findById(concertId);
        verify(categories).findById(categoryId);
    }

    @Test
    public void getAllBallotsByConcertIdAndCategoryId_Success() {
        Long concertId = 1L;
        Long categoryId = 2L;
        Ballot ballot = new Ballot();
        ballot.setId(1L);
        BallotResponseDto ballotDto = new BallotResponseDto();

        // Mock the behavior of repositories
        when(concerts.findById(concertId)).thenReturn(Optional.of(new Concert()));
        when(categories.findById(categoryId)).thenReturn(Optional.of(new Category()));
        when(ballots.findAllByConcertIdAndCategoryId(concertId, categoryId))
                .thenReturn(Collections.singletonList(ballot));
        when(modelMapper.map(any(Ballot.class), eq(BallotResponseDto.class))).thenReturn(ballotDto);

        List<BallotResponseDto> result = ballotService.getAllBallotsByConcertIdAndCategoryId(concertId, categoryId);

        // Assertions
        assertTrue(result.size() == 1);
        assertTrue(result.get(0) == ballotDto);
        verify(concerts).findById(concertId);
        verify(categories).findById(categoryId);
        verify(ballots).findAllByConcertIdAndCategoryId(concertId, categoryId);
        verify(modelMapper).map(any(Ballot.class), eq(BallotResponseDto.class));
    }

    @Test
    public void addBallot_Success() {
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(1L);
        Long concertId = 1L;
        Long categoryId = 2L;
        User user = new User();
        user.setId(1L);
        Concert concert = new Concert();
        Category category = new Category();
        BallotResponseDto ballotDto = new BallotResponseDto();

        // Mock the behavior of repositories
        when(users.findById(userDetails.getId())).thenReturn(Optional.of(user));
        when(concerts.findById(concertId)).thenReturn(Optional.of(concert));
        when(categories.findByVenueAndIdAndActiveBallotCategoriesConcert(concert.getVenue(), categoryId,
                concert)).thenReturn(Optional.of(category));
        when(ballots.save(Mockito.any(Ballot.class))).thenReturn(new Ballot());
        when(modelMapper.map(any(Ballot.class), eq(BallotResponseDto.class))).thenReturn(ballotDto);

        BallotResponseDto result = ballotService.addBallot(userDetails, concertId, categoryId);

        // Assertions
        assertTrue(result != null);

        verify(users).findById(userDetails.getId());
        verify(concerts).findById(concertId);
        verify(categories).findByVenueAndIdAndActiveBallotCategoriesConcert(concert.getVenue(), categoryId, concert);
        verify(ballots).save(Mockito.any(Ballot.class));
        verify(modelMapper).map(any(Ballot.class), eq(BallotResponseDto.class));
    }

    @Test
    public void addBallot_UserDoesNotExist_Failure() {
        // setup the mock
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(-1L);
        Long concertId = 1L;
        Long categoryId = 2L;

        when(users.findById(userDetails.getId())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.addBallot(userDetails, concertId, categoryId);
        });
        verify(users).findById(userDetails.getId());
    }

    @Test
    public void addBallot_ConcertDoesNotExist_Failure() {
        // setup the mock
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(-1L);
        Long concertId = 1L;
        Long categoryId = 2L;

        when(users.findById(userDetails.getId())).thenReturn(Optional.of(new User()));
        when(concerts.findById(concertId)).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.addBallot(userDetails, concertId, categoryId);
        });
        verify(users).findById(userDetails.getId());
        verify(concerts).findById(concertId);
    }

    @Test
    public void addBallot_CategoryDoesNotExist_Failure() {
        // setup the mock
        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.setId(-1L);
        Long concertId = 1L;
        Long categoryId = 2L;
        Concert concert = new Concert();

        when(users.findById(userDetails.getId())).thenReturn(Optional.of(new User()));
        when(concerts.findById(concertId)).thenReturn(Optional.of(concert));
        when(categories.findByVenueAndIdAndActiveBallotCategoriesConcert(null, categoryId, concert))
                .thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            ballotService.addBallot(userDetails, concertId, categoryId);
        });
        verify(users).findById(userDetails.getId());
        verify(concerts).findById(concertId);
    }

    @Test
    public void randomizeBallotForConcertIdAndCategoryId_Success() {
        Long concertId = 1L;
        Long categoryId = 2L;
        Ballot ballot = new Ballot();
        ballot.setId(1L);

        // Mock the behavior of repositories
        when(concerts.findById(concertId)).thenReturn(Optional.of(new Concert()));
        when(categories.findById(categoryId)).thenReturn(Optional.of(new Category()));
        when(ballots.findAllByConcertIdAndCategoryId(concertId, categoryId))
                .thenReturn(Collections.singletonList(ballot));

        ballotService.randomiseBallotForConcertIdAndCategoryId(concertId, categoryId);

        // Assertions
        verify(ballots).saveAll(Mockito.anyList());
        verify(concerts).findById(concertId);
        verify(categories).findById(categoryId);
        verify(ballots).findAllByConcertIdAndCategoryId(concertId, categoryId);
    }
}
