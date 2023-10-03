package com.cs203g3.ticketing.activeBallotCategory;

import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ActiveBallotCategoryService {

    private ActiveBallotCategoryRepository activeBallotCategories;
    private ConcertRepository concerts;
    private CategoryRepository categories;

    public ActiveBallotCategoryService(ActiveBallotCategoryRepository activeBallotCategories, ConcertRepository concerts, CategoryRepository categories) {
        this.activeBallotCategories = activeBallotCategories;
        this.concerts = concerts;
        this.categories = categories;
    }

    public ActiveBallotCategory addActiveBallotCategory(Long concertId, Long categoryId) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        return activeBallotCategories.save(new ActiveBallotCategory(concert, category));
    }
}
