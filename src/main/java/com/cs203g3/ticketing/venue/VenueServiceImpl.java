package com.cs203g3.ticketing.venue;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cs203g3.exception.ResourceNotFoundException;

@Service
public class VenueServiceImpl implements VenueService {
    
    private VenueRepository venues;

    public VenueServiceImpl(VenueRepository venues) {
        this.venues = venues;
    }
    
    public List<Venue> getAllVenues() {
        return venues.findAll();
    }

    public Venue getVenue(Long id) {
        return venues.findById(id).map(venue -> venue).orElseThrow(() -> new ResourceNotFoundException(Venue.class, id));
    }

    public Venue addVenue(Venue venue) {
        return venues.save(venue);
    }

    public Venue updateVenue(Long id, Venue updatedVenue) {
        return venues.findById(id).map(venue -> {
            updatedVenue.setId(id);
            return venues.save(updatedVenue);
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, id));
    }

    public void deleteVenue(Long id) {
        venues.deleteById(id);
    }
}
