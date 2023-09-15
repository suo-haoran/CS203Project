package com.cs203g3.ticketing.seat;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModelMapper modelMapper;
    
    private SeatRepository seats;
    private VenueRepository venues;
    private SectionRepository sections;

    public SeatService(SeatRepository seats, VenueRepository venues, SectionRepository sections) {
        this.seats = seats;
        this.venues = venues;
        this.sections = sections;
    }

    public List<SeatResponseDto> getAllSeatsByVenueIdAndSectionId(Long venueId, Long sectionId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        return seats.findAllBySectionVenueAndSection(venue, section).stream().map(seat -> {
            return modelMapper.map(seat, SeatResponseDto.class);
        }).collect(Collectors.toList());
    }

    public SeatResponseDto getSeatByVenueIdAndSectionIdAndSeatId(Long venueId, Long sectionId, Long seatId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        return seats.findBySectionVenueAndSectionAndId(venue, section, seatId)
            .map(seat -> modelMapper.map(seat, SeatResponseDto.class))
            .orElseThrow(() -> new ResourceNotFoundException(Seat.class, seatId));
    } 

    public Seat addSeat(Long venueId, Long sectionId, SeatRequestDto newSeatDto) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
        
        Seat newSeat = modelMapper.map(newSeatDto, Seat.class);
        newSeat.setSection(section);
        return seats.save(newSeat);
    }

    public Seat updateSeat(Long venueId, Long sectionId, Long seatId, SeatRequestDto newSeatDto) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        Section section = sections.findByVenueAndId(venue, sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
        seats.findBySectionVenueAndSectionAndId(venue, section, seatId).orElseThrow(() -> new ResourceNotFoundException(Seat.class, seatId));
        
        Seat newSeat = modelMapper.map(newSeatDto, Seat.class);
        newSeat.setId(seatId);
        newSeat.setSection(section);
        return seats.save(newSeat);
    }

    public void deleteSeat(Long venueId, Long sectionId, Long seatId) {
        seats.deleteBySectionVenueIdAndSectionIdAndId(venueId, sectionId, seatId);
    }
}
