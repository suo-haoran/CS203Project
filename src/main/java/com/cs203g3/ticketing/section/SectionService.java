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
    
    public SectionService(ModelMapper modelMapper, CategoryRepository categories, SectionRepository sections, VenueRepository venues) {
        this.modelMapper = modelMapper;
        this.categories = categories;
        this.sections = sections;
        this.venues = venues;
    }

    public Venue getVenueThrowExceptionIfNotExists(Long venueId) {
        return venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    public List<SectionResponseDto> getAllSectionsByVenueId(Long venueId) {
        Venue venue = getVenueThrowExceptionIfNotExists(venueId);

        return sections.findAllSectionsByCategoryVenue(venue).stream()
            .map(section -> modelMapper.map(section, SectionResponseDto.class))
            .collect(Collectors.toList());
    }

    public List<SectionResponseDto> getAllSectionsByVenueIdAndCategoryId(Long venueId, Long categoryId) {
        Venue venue = getVenueThrowExceptionIfNotExists(venueId);
        Category category = categories.findByVenueAndId(venue, categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        return sections.findAllSectionsByCategory(category).stream()
            .map(section -> modelMapper.map(section, SectionResponseDto.class))
            .collect(Collectors.toList());
    }

    public SectionResponseDto getSectionByVenueIdAndSectionId(Long venueId, Long sectionId) {
        Venue venue = getVenueThrowExceptionIfNotExists(venueId);

        return sections.findByCategoryVenueAndId(venue, sectionId)
            .map(section -> modelMapper.map(section, SectionResponseDto.class))
            .orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
    }
}
