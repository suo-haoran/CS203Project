package com.cs203g3.ticketing.section;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.venue.Venue;


public interface SectionRepository extends JpaRepository<Section, Long>{
    List<Section> findAllSectionsByCategory(Category category);
    List<Section> findAllSectionsByCategoryVenue(Venue venue);
    Optional<Section> findByCategoryVenueAndId(Venue venue, Long sectionId);
}
