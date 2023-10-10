package com.cs203g3.ticketing.venue;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.exception.ResourceNotFoundException;


@Service
public class VenueService {

    private VenueRepository venues;

    public VenueService(VenueRepository venues) {
        this.venues = venues;
    }

    public List<Venue> getAllVenues() {
        return venues.findAll();
    }

    public Venue getVenue(Long id) {
        return venues.findById(id).map(venue -> venue).orElseThrow(() -> new ResourceNotFoundException(Venue.class, id));
    }
}
