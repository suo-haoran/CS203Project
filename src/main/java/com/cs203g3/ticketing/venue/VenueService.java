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

    /**
     * Retrieves a list of all venues available in the system.
     *
     * @return A list of Venue objects representing all venues.
     */
    public List<Venue> getAllVenues() {
        return venues.findAll();
    }

    /**
     * Retrieves a specific venue based on its unique identifier (ID).
     *
     * @param id The ID of the venue to retrieve.
     * @return The Venue object representing the requested venue.
     * @throws ResourceNotFoundException if the venue with the specified ID does not
     *                                   exist.
     */
    public Venue getVenue(Long id) {
        return venues.findById(id).map(venue -> venue)
                .orElseThrow(() -> new ResourceNotFoundException(Venue.class, id));
    }
}
