package com.cs203g3.ticketing.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.venue.Venue;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByVenueAndId(Venue venue, Long categoryId);
}
