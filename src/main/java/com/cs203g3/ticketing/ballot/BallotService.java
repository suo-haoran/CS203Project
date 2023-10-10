package com.cs203g3.ticketing.ballot;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.ballot.dto.BallotResponseDto;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceAlreadyExistsException;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

@Service
public class BallotService {

    private ModelMapper modelMapper;

    private BallotRepository ballots;

    private CategoryRepository categories;
    private ConcertRepository concerts;
    private UserRepository users;

    public BallotService(ModelMapper modelMapper, BallotRepository ballots, CategoryRepository categories, ConcertRepository concerts, UserRepository users) {
        this.modelMapper = modelMapper;
        this.ballots = ballots;
        this.categories = categories;
        this.concerts = concerts;
        this.users = users;
    }

    public void verifyValidConcertIdAndCategoryId(Long concertId, Long categoryId) {
        concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        categories.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, concertId));
    }

    public List<BallotResponseDto> getAllBallotsByConcertIdAndCategoryId(Long concertId, Long categoryId) {
        verifyValidConcertIdAndCategoryId(concertId, categoryId);

        return ballots.findAllByConcertIdAndCategoryId(concertId, categoryId).stream()
            .map(ballot -> modelMapper.map(ballot, BallotResponseDto.class))
            .collect(Collectors.toList());
    }

    public BallotResponseDto addBallot(UserDetailsImpl userDetails, Long concertId, Long categoryId) {
        Long userId = userDetails.getId();

        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndIdAndActiveBallotCategoriesConcert(concert.getVenue(), categoryId, concert)
            .orElseThrow(() -> new ResourceNotFoundException("This category is either not available for balloting now or does not exist at all"));

        Ballot newBallot = new Ballot(user, concert, category);

        try {
            return modelMapper.map(ballots.save(newBallot), BallotResponseDto.class);
        } catch (DataIntegrityViolationException ex) {
            ConstraintViolationException cve = (ConstraintViolationException) ex.getCause();
            if (cve.getConstraintName().equals("ballot.UniqueUserConcertCategoryIdentifier")) {
                throw new ResourceAlreadyExistsException("User has already joined this balloting session");
            }

            throw ex;
        }
    }

    public void randomiseBallotForConcertIdAndCategoryId(Long concertId, Long categoryId) {
        verifyValidConcertIdAndCategoryId(concertId, categoryId);

        List<Ballot> receivedBallots = ballots.findAllByConcertIdAndCategoryId(concertId, categoryId);
        Collections.shuffle(receivedBallots);

        for (int i = 0; i < receivedBallots.size(); i++) {
            receivedBallots.get(i).setBallotResult(Long.valueOf(i));
        }

        ballots.saveAll(receivedBallots);
    }
}
