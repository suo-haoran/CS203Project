package com.cs203g3.ticketing.venue;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/venues")
public class VenueController {
    private VenueService venueService;

    public VenueController(VenueService vs) {
        this.venueService = vs;
    }

    @GetMapping
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("/{id}")
    public Venue getVenue(@PathVariable Long id) {
        return venueService.getVenue(id);
    }
}
