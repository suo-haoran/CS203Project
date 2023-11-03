package com.cs203g3.ticketing.seat;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.seat.dto.SeatResponseDto;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.section.SectionRepository;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class SeatService {

    private ModelMapper modelMapper;

    private SeatRepository seats;
    private VenueRepository venues;
    private SectionRepository sections;

    public SeatService(ModelMapper modelMapper, SeatRepository seats, VenueRepository venues,
            SectionRepository sections) {
        this.modelMapper = modelMapper;

        this.seats = seats;
        this.venues = venues;
        this.sections = sections;
    }

    /**
     * Retrieves all seats within a specified section of a venue.
     *
     * @param venueId   The ID of the venue containing the section.
     * @param sectionId The ID of the section for which to retrieve seats.
     * @return A list of seat response DTOs within the specified section.
     * @throws ResourceNotFoundException If the venue or section with the specified
     *                                   IDs is not found.
     */
    public List<SeatResponseDto> getAllSeatsByVenueIdAndSectionId(Long venueId, Long sectionId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByCategoryVenueAndId(venue, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        return seats.findAllBySectionCategoryVenueAndSection(venue, section).stream().map(seat -> {
            return modelMapper.map(seat, SeatResponseDto.class);
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves a seat within a specified section of a venue by its seat ID.
     *
     * @param venueId   The ID of the venue containing the section.
     * @param sectionId The ID of the section containing the seat.
     * @param seatId    The ID of the seat to retrieve.
     * @return The seat response DTO for the specified seat.
     * @throws ResourceNotFoundException If the venue, section, or seat with the
     *                                   specified IDs is not found.
     */
    public SeatResponseDto getSeatByVenueIdAndSectionIdAndSeatId(Long venueId, Long sectionId, Long seatId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByCategoryVenueAndId(venue, sectionId)
                .orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        return seats.findBySectionCategoryVenueAndSectionAndId(venue, section, seatId)
                .map(seat -> modelMapper.map(seat, SeatResponseDto.class))
                .orElseThrow(() -> new ResourceNotFoundException(Seat.class, seatId));
    }
}
