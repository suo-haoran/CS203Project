package com.cs203g3.ticketing.seat;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.venue.Venue;

import jakarta.transaction.Transactional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllBySectionCategoryVenue(Venue venue);
    List<Seat> findAllBySectionCategoryVenueAndSection(Venue venue, Section section);
    Optional<Seat> findBySectionCategoryVenueAndId(Venue venue, Long seatId);
    Optional<Seat> findBySectionCategoryVenueAndSectionAndId(Venue venue, Section section, Long seatId);

    @Transactional
    void deleteBySectionCategoryVenueIdAndSectionIdAndId(Long venueId, Long sectionId, Long seatId);
}
