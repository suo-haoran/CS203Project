package com.cs203g3.ticketing.seat;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.seat.dto.SeatRequestDto;
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

    public SeatService(ModelMapper modelMapper, SeatRepository seats, VenueRepository venues, SectionRepository sections) {
        this.modelMapper = modelMapper;

        this.seats = seats;
        this.venues = venues;
        this.sections = sections;
    }

    public List<SeatResponseDto> getAllSeatsByVenueIdAndSectionId(Long venueId, Long sectionId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByCategoryVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        return seats.findAllBySectionCategoryVenueAndSection(venue, section).stream().map(seat -> {
            return modelMapper.map(seat, SeatResponseDto.class);
        }).collect(Collectors.toList());
    }

    public SeatResponseDto getSeatByVenueIdAndSectionIdAndSeatId(Long venueId, Long sectionId, Long seatId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByCategoryVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        return seats.findBySectionCategoryVenueAndSectionAndId(venue, section, seatId)
            .map(seat -> modelMapper.map(seat, SeatResponseDto.class))
            .orElseThrow(() -> new ResourceNotFoundException(Seat.class, seatId));
    } 

    public Seat addSeat(Long venueId, Long sectionId, SeatRequestDto newSeatDto) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByCategoryVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
        
        Seat newSeat = modelMapper.map(newSeatDto, Seat.class);
        newSeat.setSection(section);
        return seats.save(newSeat);
    }

    public Seat updateSeat(Long venueId, Long sectionId, Long seatId, SeatRequestDto newSeatDto) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByCategoryVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
        seats.findBySectionCategoryVenueAndSectionAndId(venue, section, seatId).orElseThrow(() -> new ResourceNotFoundException(Seat.class, seatId));
        
        Seat newSeat = modelMapper.map(newSeatDto, Seat.class);
        newSeat.setId(seatId);
        newSeat.setSection(section);
        return seats.save(newSeat);
    }

    public void deleteSeat(Long venueId, Long sectionId, Long seatId) {
        seats.deleteBySectionCategoryVenueIdAndSectionIdAndId(venueId, sectionId, seatId);
    }
}
