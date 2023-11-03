package com.cs203g3.ticketing.activeBallotCategory;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.activeBallotCategory.dto.ActiveBallotCategoryRequestDto;
import com.cs203g3.ticketing.ballot.BallotService;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ActiveBallotCategoryService {

    private static Logger logger = LoggerFactory.getLogger(ActiveBallotCategoryService.class);

    private final TaskScheduler taskScheduler;

    private ActiveBallotCategoryRepository activeBallotCategories;
    private ConcertRepository concerts;
    private CategoryRepository categories;

    private BallotService ballotService;

    public ActiveBallotCategoryService(
        TaskScheduler taskScheduler, ActiveBallotCategoryRepository activeBallotCategories,
        ConcertRepository concerts, CategoryRepository categories,
        BallotService ballotService) {
        this.taskScheduler = taskScheduler;
        this.activeBallotCategories = activeBallotCategories;
        this.concerts = concerts;
        this.categories = categories;
        this.ballotService = ballotService;
    }

    /**
     * Retrieves a list of all active ballot categories in the application.
     *
     * @return A list of ActiveBallotCategory objects representing all active ballot categories.
     */
    public List<ActiveBallotCategory> getAllActiveBallotCategories() {
        return activeBallotCategories.findAll();
    }

    /**
     * Adds a new active ballot category for a specific concert and category.
     * Schedules the closure of the active ballot category based on the provided time interval.
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     * @param dto The ActiveBallotCategoryRequestDto containing the closure time.
     * @return The newly added ActiveBallotCategory object.
     * @throws ResourceNotFoundException If the specified concert or category does not exist.
     */
    private void scheduleActiveBallotClosing(ActiveBallotCategory abc, Integer secondsBeforeClosure) {
        taskScheduler.schedule(
            () -> {
                deleteActiveBallotCategory(abc);
                randomizeBallotResultsForAllSessionsInActiveBallotCategory(abc);
                scheduleOpeningNextPurchaseWindow(abc);
            },
            Instant.now().plusSeconds(secondsBeforeClosure));
    }

    private void deleteActiveBallotCategory(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Scheduled closure of ActiveBallotCategory for ConcertId<" + concertId + ">, Category<" + categoryId + ">");
        activeBallotCategories.delete(abc);
    }

    private void randomizeBallotResultsForAllSessionsInActiveBallotCategory(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Randomizing ballot results for all Sessions in ConcertId<" + concertId + ">, Category<" + categoryId + ">");
        for (ConcertSession session : abc.getConcert().getSessions()) {
            ballotService.randomiseBallotForConcertSessionIdAndCategoryId(session.getId(), categoryId);
        }
    }

    private void scheduleOpeningNextPurchaseWindow(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Scheduling purchase window for all Sessions in ConcertId<" + concertId + ">, Category<" + categoryId + ">");
        for (ConcertSession session : abc.getConcert().getSessions()) {
            ballotService.openNextPurchaseWindow(session.getId(), categoryId);
        }
    }

    public ActiveBallotCategory addActiveBallotCategory(Long concertId, Long categoryId, ActiveBallotCategoryRequestDto dto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndId(concert.getVenue(), categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        ActiveBallotCategory activeBallotCategory = activeBallotCategories.save(new ActiveBallotCategory(concert, category));
        scheduleActiveBallotClosing(activeBallotCategory, dto.getSecondsBeforeClosure());

        return activeBallotCategory;
    }

    // public void deleteActiveBallotCategory(Long concertId, Long categoryId) {
    //     activeBallotCategories.deleteByConcertIdAndCategoryId(concertId, categoryId);
    // }
}
