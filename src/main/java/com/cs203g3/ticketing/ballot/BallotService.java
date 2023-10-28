package com.cs203g3.ticketing.ballot;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.exception.ConstraintViolationException;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.ballot.dto.BallotResponseDto;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.concertSession.ConcertSessionRepository;
import com.cs203g3.ticketing.email.EmailService;
import com.cs203g3.ticketing.exception.ResourceAlreadyExistsException;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;
import com.cs203g3.ticketing.ticket.TicketRepository;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

@Service
public class BallotService {

    @Value("${cs203.frontend.url}")
    private String frontendUrl;

    private static Logger logger = LoggerFactory.getLogger(BallotService.class);

    private ModelMapper modelMapper;
    private TaskScheduler taskScheduler;

    private BallotRepository ballots;

    private CategoryRepository categories;
    private ConcertSessionRepository concertSessions;
    private TicketRepository tickets;
    private UserRepository users;

    private EmailService emailService;

    private final int WINDOW_ROTATION_IN_SECONDS = 60 * 60 * 24; // 24 hours

    public BallotService(
        ModelMapper modelMapper, TaskScheduler taskScheduler,
        BallotRepository ballots, CategoryRepository categories, ConcertSessionRepository concertSessions,
        TicketRepository tickets, UserRepository users,
        EmailService emailService) {
        this.modelMapper = modelMapper;
        this.taskScheduler = taskScheduler;

        this.ballots = ballots;
        this.categories = categories;
        this.concertSessions = concertSessions;
        this.tickets = tickets;
        this.users = users;

        this.emailService = emailService;
    }

    public void verifyValidConcertSessionIdAndCategoryId(Long concertSessionId, Long categoryId) {
        ConcertSession cs = concertSessions.findById(concertSessionId)
            .orElseThrow(() -> new ResourceNotFoundException(ConcertSession.class, concertSessionId));
        categories.findByVenueAndId(cs.getConcert().getVenue(), categoryId)
            .orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));
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

    // Set purchaseAllowed to WINDOW_OVER for ballots in the current window
    // Current window => Ballots with purchaseAllowed = ALLOWED
    private void closeCurrentPurchaseWindow(Long concertSessionId, Long categoryId) {
        List<Ballot> ballotsInWindow = ballots.findAllByConcertSessionIdAndCategoryIdAndPurchaseAllowedOrderByBallotResultAsc(
            concertSessionId, categoryId, EnumPurchaseAllowed.ALLOWED);

        for (Ballot ballot : ballotsInWindow) {
            ballot.setPurchaseAllowed(EnumPurchaseAllowed.WINDOW_OVER);
        }
        ballots.saveAll(ballotsInWindow);

        logger.info("Window for sessionId #<" + concertSessionId + "> closed for <" + ballotsInWindow.size() + "> users");
    }

    // Set purchaseAllowed to ALLOWED for the next X # of ballots that have purchaseAllowed = NOT_YET
    // X = # of seats still left unpurchased
    // Also closes the current purchase window
    public void openNextPurchaseWindow(Long concertSessionId, Long categoryId) {
        verifyValidConcertSessionIdAndCategoryId(concertSessionId, categoryId);
        closeCurrentPurchaseWindow(concertSessionId, categoryId);

        Integer availableSeats = tickets.countByConcertSessionIdAndReceiptIsNull(concertSessionId);

        List<Ballot> ballotsForNextWindow = ballots.findAllByConcertSessionIdAndCategoryIdAndPurchaseAllowedOrderByBallotResultAsc(
            concertSessionId, categoryId, EnumPurchaseAllowed.NOT_YET, PageRequest.of(0, availableSeats));

        if (availableSeats <= 0 || ballotsForNextWindow.size() <= 0) {
            logger.info("No more available seats/ballots for next window, stopping here");
            return;
        }

        for (Ballot ballot : ballotsForNextWindow) {
            ballot.setPurchaseAllowed(EnumPurchaseAllowed.ALLOWED);
            emailService.sendBallotingSuccessEmail(
                ballot.getUser(),
                ballot.getConcertSession(), 
                String.format("https://%s/ticket?concertSession=%ld&category=%ld", frontendUrl, concertSessionId, categoryId)
            );
        }
        ballots.saveAll(ballotsForNextWindow);
        logger.info("Window for sessionId #<" + concertSessionId + "> successfully opened for next <" + ballotsForNextWindow.size() + "> users");

        taskScheduler.schedule(() -> {
            openNextPurchaseWindow(concertSessionId, categoryId);
        }, Instant.now().plusSeconds(WINDOW_ROTATION_IN_SECONDS));
        logger.info("Next window rotation for sessionId #<" + concertSessionId + "> scheduled in " + WINDOW_ROTATION_IN_SECONDS + " seconds");
    }
}
