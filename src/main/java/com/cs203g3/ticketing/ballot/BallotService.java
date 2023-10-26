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
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.concertSession.ConcertSessionRepository;
import com.cs203g3.ticketing.email.EmailService;
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
    private ConcertSessionRepository concertSessions;
    private UserRepository users;

    private EmailService emailService;

    public BallotService(
        ModelMapper modelMapper, BallotRepository ballots,
        CategoryRepository categories, ConcertSessionRepository concertSessions,
        UserRepository users, EmailService emailService) {
        this.modelMapper = modelMapper;
        this.ballots = ballots;
        this.categories = categories;
        this.concertSessions = concertSessions;
        this.users = users;
        this.emailService = emailService;
    }

    public void verifyValidConcertSessionIdAndCategoryId(Long concertSessionId, Long categoryId) {
        concertSessions.findById(concertSessionId).orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertSessionId));
        categories.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));
    }

    public List<BallotResponseDto> getAllBallotsByConcertSessionIdAndCategoryId(Long concertSessionId, Long categoryId) {
        verifyValidConcertSessionIdAndCategoryId(concertSessionId, categoryId);

        return ballots.findAllByConcertSessionIdAndCategoryId(concertSessionId, categoryId).stream()
            .map(ballot -> modelMapper.map(ballot, BallotResponseDto.class))
            .collect(Collectors.toList());
    }

    public BallotResponseDto addBallot(UserDetailsImpl userDetails, Long concertSessionId, Long categoryId) {
        Long userId = userDetails.getId();

        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        ConcertSession concertSession = concertSessions.findById(concertSessionId)
            .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertSessionId));
        Category category = categories
            .findByVenueAndIdAndActiveBallotCategoriesConcertSessions(concertSession.getConcert().getVenue(), categoryId, concertSession)
            .orElseThrow(() -> new ResourceNotFoundException("This category is either not available for balloting now or does not exist at all"));

        Ballot newBallot = new Ballot(user, concertSession, category);

        try {
            BallotResponseDto result = modelMapper.map(ballots.save(newBallot), BallotResponseDto.class);
            emailService.sendBallotingConfirmationEmail(user, concertSession);
            return result;
        } catch (DataIntegrityViolationException ex) {
            ConstraintViolationException cve = (ConstraintViolationException) ex.getCause();
            if (cve.getConstraintName().equals("ballot.UniqueUserConcertCategoryIdentifier")) {
                throw new ResourceAlreadyExistsException("User has already joined this balloting session");
            }

            throw ex;
        }
    }

    public void randomiseBallotForConcertSessionIdAndCategoryId(Long concertSessionId, Long categoryId) {
        verifyValidConcertSessionIdAndCategoryId(concertSessionId, categoryId);

        List<Ballot> receivedBallots = ballots.findAllByConcertSessionIdAndCategoryId(concertSessionId, categoryId);
        Collections.shuffle(receivedBallots);

        for (int i = 0; i < receivedBallots.size(); i++) {
            Ballot receivedBallot = receivedBallots.get(i);
            receivedBallot.setBallotResult(Long.valueOf(i));
        }

        ballots.saveAll(receivedBallots);
    }
}
