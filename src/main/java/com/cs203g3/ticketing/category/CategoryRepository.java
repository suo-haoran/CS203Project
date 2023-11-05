package com.cs203g3.ticketing.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.activeBallotCategory.EnumActiveBallotCategoryStatus;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.venue.Venue;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByVenue(Venue venue);

    Optional<Category> findByVenueAndId(Venue venue, Long categoryId);

    Optional<Category> findByVenueAndIdAndActiveBallotCategoriesConcertSessionsAndActiveBallotCategoriesStatus(
        Venue venue, Long categoryId, ConcertSession concertSession, EnumActiveBallotCategoryStatus status);
}
