package com.cs203g3.ticketing.venue;

import java.util.List;

public interface VenueService {
    List<Venue> getAllVenues();
    Venue getVenue(Long id);
    Venue addVenue(Venue venue);
    Venue updateVenue(Long id, Venue updatedVenue);
    void deleteVenue(Long id);
}
