package com.cs203g3.ticketing.concert;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueNotFoundException;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class ConcertServiceImpl implements ConcertService {

    private ConcertRepository concerts;
    private VenueRepository venues;

    public ConcertServiceImpl(ConcertRepository concerts, VenueRepository venues) {
        this.concerts = concerts;
        this.venues = venues;
    }

    @Override
    public List<Concert> getAllConcerts() {
        return concerts.findAll();
    }

    @Override
    public Concert getConcert(Long id) {
        return concerts.findById(id).map(concert -> concert).orElseThrow(() -> new ConcertNotFoundException(id));
    } 

    @Override
    public Concert addConcert(Concert newConcert) {
        Long venueId = newConcert.getVenue().getId();
        venues.findById(venueId).map(venue -> {
            newConcert.setVenue(venue);
            return concerts.save(newConcert);
        }).orElseThrow(() -> new VenueNotFoundException(venueId));

        return newConcert;
    }

    @Override
    public Concert updateConcert(Long id, Concert updatedConcert) {
        return concerts.findById(id).map(concert -> {
            updatedConcert.setId(id);
            return concerts.save(updatedConcert);
        }).orElseThrow(() -> new ConcertNotFoundException(id));
    }

    @Override
    public void deleteConcert(Long id) {
        concerts.findById(id).map(concert -> {
            concerts.delete(concert);
            return concert;
        }).orElseThrow(() -> new ConcertNotFoundException(id));
    }
}