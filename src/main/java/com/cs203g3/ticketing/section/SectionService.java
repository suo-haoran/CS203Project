package com.cs203g3.ticketing.section;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.section.dto.SectionResponseDto;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class SectionService {

    private ModelMapper modelMapper;

    private CategoryRepository categories;
    private SectionRepository sections;
    private VenueRepository venues;

    public SectionService(ModelMapper modelMapper, CategoryRepository categories, SectionRepository sections,
            VenueRepository venues) {
        this.modelMapper = modelMapper;
        this.categories = categories;
        this.sections = sections;
        this.venues = venues;
    }

    /**
     * Retrieves a venue by its ID and throws an exception if it doesn't exist.
     *
     * @param venueId The ID of the venue to retrieve.
     * @return The venue with the specified ID.
     * @throws ResourceNotFoundException If the venue with the specified ID is not
     *                                   found.
     */
    public Venue getVenueThrowExceptionIfNotExists(Long venueId) {
        return venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    /**
     * Retrieves all sections within a specified venue.
     *
     * @param venueId The ID of the venue for which to retrieve sections.
     * @return A list of section response DTOs within the specified venue.
     * @throws ResourceNotFoundException If the venue with the specified ID is not
     *                                   found.
     */
    public List<SectionResponseDto> getAllSectionsByVenueId(Long venueId) {
        Venue venue = getVenueThrowExceptionIfNotExists(venueId);

        return sections.findAllSectionsByCategoryVenue(venue).stream()
                .map(section -> modelMapper.map(section, SectionResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all sections within a specified venue and category.
     *
     * @param venueId    The ID of the venue for which to retrieve sections.
     * @param categoryId The ID of the category for which to retrieve sections.
     * @return A list of section response DTOs within the specified venue and
     *         category.
     * @throws ResourceNotFoundException If the venue or category with the specified
     *                                   IDs is not found.
     */
    public List<SectionResponseDto> getAllSectionsByVenueIdAndCategoryId(Long venueId, Long categoryId) {
        Venue venue = getVenueThrowExceptionIfNotExists(venueId);
        Category category = categories.findByVenueAndId(venue, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        return sections.findAllSectionsByCategory(category).stream()
                .map(section -> modelMapper.map(section, SectionResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a section within a specified venue by its section ID.
     *
     * @param venueId   The ID of the venue containing the section.
     * @param sectionId The ID of the section to retrieve.
     * @return The section response DTO for the specified section.
     * @throws ResourceNotFoundException If the venue or section with the specified
     *                                   IDs is not found.
     */
    public SectionResponseDto getSectionByVenueIdAndSectionId(Long venueId, Long sectionId) {
        Venue venue = getVenueThrowExceptionIfNotExists(venueId);

        return sections.findByCategoryVenueAndId(venue, sectionId)
                .map(section -> modelMapper.map(section, SectionResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
    }
}
