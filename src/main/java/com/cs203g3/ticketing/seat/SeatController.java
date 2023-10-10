package com.cs203g3.ticketing.seat;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.seat.dto.SeatResponseDto;


@RestController
@RequestMapping("/venues/{venueId}/sections/{sectionId}/seats")
public class SeatController {

    private SeatService seatService;    

    public SeatController(SeatService ss) {
        this.seatService = ss;
    }

    @GetMapping
    public List<SeatResponseDto> getAllSeatsByVenueIdAndSectionId(@PathVariable Long venueId, @PathVariable Long sectionId) {
        return seatService.getAllSeatsByVenueIdAndSectionId(venueId, sectionId);
    }

    @GetMapping("/{seatId}")
    public SeatResponseDto getSeatByVenueIdAndSectionIdAndSeatId(@PathVariable Long venueId, @PathVariable Long sectionId, @PathVariable Long seatId) {
        return seatService.getSeatByVenueIdAndSectionIdAndSeatId(venueId, sectionId, seatId);
    }
}
