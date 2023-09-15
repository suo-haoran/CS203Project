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

    public Venue addVenue(Venue newVenue) {
        return venues.save(newVenue);
    }

    public Venue updateVenue(Long id, Venue newVenue) {
        return venues.findById(id).map(venue -> {
            newVenue.setId(id);
            return venues.save(newVenue);
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, id));
    }

    public void deleteVenue(Long id) {
        venues.deleteById(id);
    }
}
