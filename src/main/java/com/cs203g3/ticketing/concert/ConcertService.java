package com.cs203g3.ticketing.concert;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.dto.ConcertRequestDto;
import com.cs203g3.ticketing.concert.dto.ConcertResponseDto;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class ConcertService {

    private ModelMapper modelMapper;

    private ConcertRepository concerts;
    private VenueRepository venues;

    public ConcertService(ModelMapper modelMapper, ConcertRepository concerts, VenueRepository venues) {
        this.modelMapper = modelMapper;

        this.concerts = concerts;
        this.venues = venues;
    }

    public List<ConcertResponseDto> getAllConcerts() {
        return concerts.findAll().stream()
            .map(concert -> modelMapper.map(concert, ConcertResponseDto.class))
            .collect(Collectors.toList());
    }

    public ConcertResponseDto getConcert(Long id) {
        return concerts.findById(id).map(concert -> {
            return modelMapper.map(concert, ConcertResponseDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, id));
    } 

    public Concert addConcert(ConcertRequestDto concertRequestDto) {
        Long venueId = concertRequestDto.getVenueId();
        Concert newConcert = modelMapper.map(concertRequestDto, Concert.class);

        return venues.findById(venueId).map(venue -> {
            newConcert.setVenue(venue);
            return concerts.save(newConcert);
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    public Concert updateConcert(Long id, ConcertRequestDto concertRequestDto) {
        Long venueId = concertRequestDto.getVenueId();
        Concert newConcert = modelMapper.map(concertRequestDto, Concert.class);

        return concerts.findById(id).map(concert -> {
            return venues.findById(venueId).map(venue -> {
                newConcert.setId(id);
                newConcert.setVenue(venue);
                return concerts.save(newConcert);
            }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, id));
    }

    public void deleteConcert(Long id) {
        concerts.deleteById(id);
    }
}