package com.cs203g3.ticketing.venue;

import java.util.List;

import org.springframework.stereotype.Service;

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
        return venues.findById(id).map(venue -> venue).orElseThrow(() -> new VenueNotFoundException(id));
    }

    public Venue addVenue(Venue venue) {
        return venues.save(venue);
    }

    public Venue updateVenue(Long id, Venue updatedVenue) {
        return venues.findById(id).map(venue -> {
            updatedVenue.setId(id);
            return venues.save(updatedVenue);
        }).orElseThrow(() -> new VenueNotFoundException(id));
    }

    public void deleteVenue(Long id) {
        venues.findById(id).map(venue -> {
            venues.delete(venue);
            return venue;
        }).orElseThrow(() -> new VenueNotFoundException(id));
    }
}
