package com.cs203g3.ticketing.section;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.venue.Venue;

import jakarta.transaction.Transactional;

public interface SectionRepository extends JpaRepository<Section, Long>{
    List<Section> findAllSectionsByVenue(Venue venue);
    Optional<Section> findByVenueAndId(Venue venue, Long sectionId);

    @Transactional
    void deleteByVenueIdAndId(Long venueId, Long sectionId);
}
