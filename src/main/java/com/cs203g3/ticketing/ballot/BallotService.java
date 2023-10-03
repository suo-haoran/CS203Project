package com.cs203g3.ticketing.ballot;

import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.security.auth.UserDetailsImpl;
import com.cs203g3.ticketing.user.User;
import com.cs203g3.ticketing.user.UserRepository;

@Service
public class BallotService {

    private BallotRepository ballots;

    private CategoryRepository categories;
    private ConcertRepository concerts;
    private UserRepository users;

    public BallotService(BallotRepository ballots, CategoryRepository categories, ConcertRepository concerts, UserRepository users) {
        this.ballots = ballots;

        this.categories = categories;
        this.concerts = concerts;
        this.users = users;
    }

    public Ballot addBallot(UserDetailsImpl userDetails, Long concertId, Long categoryId) {
        Long userId = userDetails.getId();

        User user = users.findById(userId).orElseThrow(() -> new ResourceNotFoundException(User.class, userId));
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndIdAndActiveBallotCategoriesConcert(concert.getVenue(), categoryId, concert).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        Ballot newBallot = new Ballot(user, concert, category);
        return ballots.save(newBallot);
    }
}
