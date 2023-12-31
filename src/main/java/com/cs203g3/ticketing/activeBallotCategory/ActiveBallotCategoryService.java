package com.cs203g3.ticketing.activeBallotCategory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.activeBallotCategory.dto.ActiveBallotCategoryRequestDto;
import com.cs203g3.ticketing.activeBallotCategory.dto.ActiveBallotCategoryWithTimerResponseDto;
import com.cs203g3.ticketing.activeBallotCategory.dto.BallotWindowOpeningDelayDto;
import com.cs203g3.ticketing.ballot.BallotService;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.exception.ResourceAlreadyExistsException;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ActiveBallotCategoryService {

    private static Logger logger = LoggerFactory.getLogger(ActiveBallotCategoryService.class);
    private final int SECONDS_BEFORE_FIRST_WINDOW = 60 * 60 * 48; // 48 hours
    private final int SECONDS_PER_WINDOW_ROTATION = 60 * 60 * 24; // 24 hours

    private ModelMapper modelMapper;
    private final TaskScheduler taskScheduler;
    private Map<ActiveBallotCategory, ScheduledFuture<?>> scheduledTasksByAbc;

    private ActiveBallotCategoryRepository activeBallotCategories;
    private ConcertRepository concerts;
    private CategoryRepository categories;

    private BallotService ballotService;

    public ActiveBallotCategoryService(
        ModelMapper modelMapper, TaskScheduler taskScheduler,
        ActiveBallotCategoryRepository activeBallotCategories,
        ConcertRepository concerts, CategoryRepository categories,
        BallotService ballotService) {
        this.modelMapper = modelMapper;
        this.taskScheduler = taskScheduler;

        this.activeBallotCategories = activeBallotCategories;
        this.concerts = concerts;
        this.categories = categories;

        this.ballotService = ballotService;

        this.scheduledTasksByAbc = new HashMap<>();
    }

    /**
     * Retrieves a list of all active ballot categories in the application.
     *
     * @return A list of ActiveBallotCategory objects representing all active ballot categories.
     */
    public List<ActiveBallotCategoryWithTimerResponseDto> getAllActiveBallotCategories() {
        return activeBallotCategories.findAll().stream()
            .map(abc -> {
                // Sets the timer value into the DTO before returning
                ActiveBallotCategoryWithTimerResponseDto dto = modelMapper.map(abc, ActiveBallotCategoryWithTimerResponseDto.class);

                ScheduledFuture<?> task = scheduledTasksByAbc.get(abc);
                if (task != null) {
                    dto.setTimeToNextStatus(task.getDelay(TimeUnit.SECONDS));
                }

                return dto;
            })
            .collect(Collectors.toList());
    }

    /**
     * Schedules the closing of an active ballot cateogory
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
                scheduleOpeningNextPurchaseWindow(abc, SECONDS_BEFORE_FIRST_WINDOW);
            },
            Instant.now().plusSeconds(secondsBeforeClosure));

        scheduledTasksByAbc.put(abc, task);
        logger.info("Closing of active ballot window for all Sessions in ConcertId<"+ abc.getConcert().getId() +">, Category<"+ abc.getCategory().getId() +"> scheduled in "+ secondsBeforeClosure +" seconds");
    }

    /**
     * Closes the ActiveBallotCategory (set status to AWAITING_FIRST_PURCHASE_WINDOW)
     *
     * @param abc The ActiveBallotCategory to be closed
     * @return void
     */
    private void closeActiveBallotCategoryScheduled(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Closing ActiveBallotCategory for ConcertId<" + concertId + ">, Category<" + categoryId + ">");

        abc.setStatus(EnumActiveBallotCategoryStatus.AWAITING_FIRST_PURCHASE_WINDOW);
        activeBallotCategories.save(abc);
    }


    /**
     * Shuffles the ballot results for all submitted ballot
     * Sets the result in the "ballotResult" field for the Ballot entity
     *
     * @param abc The ActiveBallotCategory with the Ballots to be shuffled
     * @return void
     */
    private void randomizeBallotResultsForAllSessionsInActiveBallotCategory(ActiveBallotCategory abc) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        logger.info("Randomizing ballot results for all Sessions in ConcertId<" + concertId + ">, Category<" + categoryId + ">");
        for (ConcertSession session : abc.getConcert().getSessions()) {
            ballotService.randomiseBallotForConcertSessionIdAndCategoryId(session.getId(), categoryId);
        }
    }

    /**
     * Schedules the opening of the next purchase window for this ActiveBallotCategory
     *
     * @param abc The ActiveBallotCategory for which the purchase window should be opened
     * @param secondsBeforeNextWindow No. of seconds before task (opening next window) is ran
     * @return void
     */
    private void scheduleOpeningNextPurchaseWindow(ActiveBallotCategory abc, Integer secondsBeforeNextWindow) {
        Long concertId = abc.getConcert().getId();
        Long categoryId = abc.getCategory().getId();

        ScheduledFuture<?> task = taskScheduler.schedule(
            () -> {
                abc.setStatus(EnumActiveBallotCategoryStatus.RUNNING_PURCHASE_WINDOWS);
                activeBallotCategories.save(abc);

                boolean windowStillRunning = false;
                for (ConcertSession session : abc.getConcert().getSessions()) {
                    // OR condition must be in this order, or else windowStillRunning will shortcircuit the boolean check
                    windowStillRunning = ballotService.openNextPurchaseWindow(session.getId(), categoryId) || windowStillRunning;
                }

                if (windowStillRunning) {
                    scheduleOpeningNextPurchaseWindow(abc, SECONDS_PER_WINDOW_ROTATION);
                } else {
                    abc.setStatus(EnumActiveBallotCategoryStatus.COMPLETED);
                    activeBallotCategories.save(abc);
                    scheduledTasksByAbc.remove(abc);
                }
            },
            Instant.now().plusSeconds(secondsBeforeNextWindow));

        scheduledTasksByAbc.put(abc, task);
        logger.info("Next purchase window for all Sessions in ConcertId<"+ concertId +">, Category<"+ categoryId +"> scheduled in "+ secondsBeforeNextWindow +" seconds");
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
        activeBallotCategories.findByConcertIdAndCategoryIdAndStatusNotIn(concertId, categoryId, List.of(EnumActiveBallotCategoryStatus.COMPLETED))
            .ifPresent(value -> {
                throw new ResourceAlreadyExistsException("An ActiveBallotCategory for ConcertID<"+ concertId +"> and CategoryID<"+ categoryId +"> is still in process, please wait until it is completed.");
            });

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
    public void adjustScheduledNextPurchaseWindowOpening(Long concertId, Long categoryId, BallotWindowOpeningDelayDto dto) {
        ActiveBallotCategory abc = activeBallotCategories
            .findByConcertIdAndCategoryIdAndStatusIn(concertId, categoryId,
                List.of(EnumActiveBallotCategoryStatus.AWAITING_FIRST_PURCHASE_WINDOW, EnumActiveBallotCategoryStatus.RUNNING_PURCHASE_WINDOWS))
            .orElseThrow(() -> new ResourceNotFoundException("This category is neither AWAITING_FIRST_PURCHASE_WINDOW nor RUNNING_PURCHASE_WINDOWS or might not exist at all"));
        Hibernate.initialize(abc.getConcert().getSessions());

        // Cancel scheduled task
        ScheduledFuture<?> task = scheduledTasksByAbc.get(abc);
        if (task != null) task.cancel(false);

        scheduleOpeningNextPurchaseWindow(abc, dto.getSecondsBeforeOpening());
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
    public void adjustScheduledActiveBallotCategoryClosing(Long concertId, Long categoryId, ActiveBallotCategoryRequestDto dto) {
        ActiveBallotCategory abc = activeBallotCategories
            .findByConcertIdAndCategoryIdAndStatusIn(concertId, categoryId,
                List.of(EnumActiveBallotCategoryStatus.ACTIVE))
            .orElseThrow(() -> new ResourceNotFoundException("This category either is not in active balloting now or does not exist at all"));

        // Cancel scheduled task
        ScheduledFuture<?> task = scheduledTasksByAbc.get(abc);
        if (task != null) task.cancel(false);

        scheduleActiveBallotClosing(abc, dto.getSecondsBeforeClosure());
    }
}
