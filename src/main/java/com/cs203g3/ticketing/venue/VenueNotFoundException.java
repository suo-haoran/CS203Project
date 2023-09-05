package com.cs203g3.ticketing.venue;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(Long id) {
        super("Could not find Venue with ID #" + id);
    }
}
