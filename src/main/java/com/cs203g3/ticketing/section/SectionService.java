package com.cs203g3.ticketing.section;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.section.dto.SectionIdAndNameDto;
import com.cs203g3.ticketing.section.dto.SectionRequestDto;
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

    public List<SectionIdAndNameDto> getAllSectionsByVenueId(Long venueId) {
        return venues.findById(venueId).map(venue -> {
            return sections.findAllSectionsByVenue(venue).stream()
                .map(section -> modelMapper.map(section, SectionIdAndNameDto.class))
                .collect(Collectors.toList());
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    public SectionIdAndNameDto getSectionByVenueIdAndSectionId(Long venueId, Long sectionId) {
        return venues.findById(venueId).map(venue -> {
            return modelMapper.map(
                sections.findByVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId)),
                SectionIdAndNameDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    public List<Section> getAllSections() {
        return sections.findAll();
    }

    public Section getSection(Long id) {
        return sections.findById(id).orElseThrow(() -> new ResourceNotFoundException(Section.class, id));
    }

    public Section addSectionByVenueId(Long venueId, SectionRequestDto newSectionDto) {
        Section newSection = modelMapper.map(newSectionDto, Section.class);

        return venues.findById(venueId).map(venue -> {
            newSection.setVenue(venue);
            return sections.save(newSection);
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    public Section updateSectionByVenueIdAndSectionId(Long venueId, Long sectionId, SectionRequestDto newSectionDto) {
        Section newSection = modelMapper.map(newSectionDto, Section.class); 

        return venues.findById(venueId).map(venue -> {
            return sections.findById(sectionId).map(section -> {
                newSection.setId(sectionId);
                newSection.setVenue(venue);
                return sections.save(newSection);
            }).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    public void deleteSectionByVenueIdAndSectionId(Long venueId, Long sectionId) {
        sections.deleteByVenueIdAndId(venueId, sectionId);
    }
}
