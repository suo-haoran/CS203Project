package com.cs203g3.ticketing.activeBallotCategory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.activeBallotCategory.dto.ActiveBallotCategoryRequestDto;
import com.cs203g3.ticketing.activeBallotCategory.dto.BallotWindowOpeningDelayDto;
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
    private final int SECONDS_BEFORE_FIRST_WINDOW = 60 * 60 * 48; // 48 hours

    private final TaskScheduler taskScheduler;
    private Map<ActiveBallotCategory, ScheduledFuture<?>> abcClosingSchedule;
    private Map<ActiveBallotCategory, ScheduledFuture<?>> ballotWindowOpeningSchedule;

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

        this.abcClosingSchedule = new HashMap<>();
        this.ballotWindowOpeningSchedule = new HashMap<>();
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
     * Handles the scheduled closing of an active ballot cateogory
     *
     * @param abc The ActiveBallotCategory to be closed
     * @param secondsBeforeClosure No. of seconds before task (closing ABC) is ran
     * @return void
     */
    private void scheduleActiveBallotClosing(ActiveBallotCategory abc, Integer secondsBeforeClosure) {
        // This line fetches all the ConcertSessions before the Hibernate transaction closes
        Hibernate.initialize(abc.getConcert().getSessions());

        ScheduledFuture<?> task = taskScheduler.schedule(
            () -> {
                randomizeBallotResultsForAllSessionsInActiveBallotCategory(abc);
                closeActiveBallotCategoryScheduled(abc);
                scheduleOpeningFirstPurchaseWindow(abc, SECONDS_BEFORE_FIRST_WINDOW);
            },
            Instant.now().plusSeconds(secondsBeforeClosure));

        abcClosingSchedule.put(abc, task);
    }

    private void closeActiveBallotCategoryScheduled(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Closing ActiveBallotCategory for ConcertId<" + concertId + ">, Category<" + categoryId + ">");

        abc.setStatus(EnumActiveBallotCategoryStatus.AWAITING_FIRST_PURCHASE_WINDOW);
        activeBallotCategories.save(abc);
    }

    private void randomizeBallotResultsForAllSessionsInActiveBallotCategory(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Randomizing ballot results for all Sessions in ConcertId<" + concertId + ">, Category<" + categoryId + ">");
        for (ConcertSession session : abc.getConcert().getSessions()) {
            ballotService.randomiseBallotForConcertSessionIdAndCategoryId(session.getId(), categoryId);
        }
    }

    private void scheduleOpeningFirstPurchaseWindow(ActiveBallotCategory abc, Integer secondsBeforeFirstWindow) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Scheduling first purchase window for all Sessions in ConcertId<" + concertId + ">, Category<" + categoryId + "> in " + secondsBeforeFirstWindow + " seconds");

        ScheduledFuture<?> task = taskScheduler.schedule(
            () -> {
                abc.setStatus(EnumActiveBallotCategoryStatus.RUNNING_PURCHASE_WINDOWS);
                activeBallotCategories.save(abc);

                for (ConcertSession session : abc.getConcert().getSessions()) {
                    ballotService.openNextPurchaseWindow(session.getId(), categoryId);
                }
            },
            Instant.now().plusSeconds(secondsBeforeFirstWindow));

        ballotWindowOpeningSchedule.put(abc, task);
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
    public ActiveBallotCategory addActiveBallotCategory(Long concertId, Long categoryId, ActiveBallotCategoryRequestDto dto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndId(concert.getVenue(), categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        ActiveBallotCategory activeBallotCategory = activeBallotCategories.save(new ActiveBallotCategory(concert, category));
        scheduleActiveBallotClosing(activeBallotCategory, dto.getSecondsBeforeClosure());

        return activeBallotCategory;
    }

    /**
     * Adjusts the time for the scheduled opening to X number of seconds
     * of the balloting window in this ActiveBallotCategory
     *      - Cancels the old task
     *      - Schedules a new opening in X seconds
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     * @return void
     * @throws ResourceNotFoundException If the specified ActiveBallotCategory does not exist.
     */
    public void adjustScheduledFirstPurchaseWindowOpening(Long concertId, Long categoryId, BallotWindowOpeningDelayDto dto) {
        ActiveBallotCategory abc = activeBallotCategories.findByConcertIdAndCategoryIdAndStatus(concertId, categoryId, EnumActiveBallotCategoryStatus.AWAITING_FIRST_PURCHASE_WINDOW)
            .orElseThrow(() -> new ResourceNotFoundException("This category is either not AWAITING_FIRST_PURCHASE_WINDOW or does not exist at all"));
        Hibernate.initialize(abc.getConcert().getSessions());

        // Cancel scheduled task
        ScheduledFuture<?> task = ballotWindowOpeningSchedule.get(abc);
        if (task != null) task.cancel(false);

        scheduleOpeningFirstPurchaseWindow(abc, dto.getSecondsBeforeOpening());
    }

    /**
     * Cancel the closure tasks for this ActiveBallotCategory
     *      - Randomize ballot
     *      - Schedule opening of first window
     * The ActiveBallotCategory is also set to the next status 'AWAITING_FIRST_BALLOT_WINDOW'
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     * @return void
     * @throws ResourceNotFoundException If the specified ActiveBallotCategory does not exist.
     */
    public void handleScheduledTasksAndDeleteActiveBallotCategory(Long concertId, Long categoryId) {
        ActiveBallotCategory abc = activeBallotCategories.findByConcertIdAndCategoryIdAndStatus(concertId, categoryId, EnumActiveBallotCategoryStatus.ACTIVE)
            .orElseThrow(() -> new ResourceNotFoundException("This category either is not in active balloting now or does not exist at all"));

        // Cancel scheduled task
        ScheduledFuture<?> task = abcClosingSchedule.get(abc);
        if (task != null) task.cancel(false);

        // Handle closing of ballot window
        randomizeBallotResultsForAllSessionsInActiveBallotCategory(abc);
        closeActiveBallotCategoryScheduled(abc);
        scheduleOpeningFirstPurchaseWindow(abc, SECONDS_BEFORE_FIRST_WINDOW);
    }
}
