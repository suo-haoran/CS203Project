package com.cs203g3.ticketing.section;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.section.dto.SectionResponseDto;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class SectionService {
    
    @Autowired
    private ModelMapper modelMapper;

    private SectionRepository sections;
    private VenueRepository venues;
    
    public SectionService(SectionRepository sections, VenueRepository venues) {
        this.sections = sections;
        this.venues = venues;
    }

    public List<SectionResponseDto> getAllSectionsByVenueId(Long venueId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));

        return sections.findAllSectionsByCategoryVenue(venue).stream()
            .map(section -> modelMapper.map(section, SectionResponseDto.class))
            .collect(Collectors.toList());
    }

    public SectionResponseDto getSectionByVenueIdAndSectionId(Long venueId, Long sectionId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));

        return sections.findByCategoryVenueAndId(venue, sectionId)
            .map(section -> modelMapper.map(section, SectionResponseDto.class))
            .orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
    }
}
