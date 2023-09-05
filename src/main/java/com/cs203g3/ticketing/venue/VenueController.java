package com.cs203g3.ticketing.venue;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    public Venue getVenue(Long id) {
        return venueService.getVenue(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Venue addVenue(@RequestBody Venue venue) {
        return venueService.addVenue(venue);
    }

    @PutMapping("/{id}")
    public Venue updateVenue(@PathVariable Long id, @RequestBody Venue venue) {
        return venueService.updateVenue(id, venue);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
    }
}
