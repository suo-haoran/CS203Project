package com.cs203g3.ticketing.seat;

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

import com.cs203g3.ticketing.seat.dto.SeatRequestDto;
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

    @PostMapping
    @ResponseStatus(code=HttpStatus.CREATED)
    public Seat addSeat(@PathVariable Long venueId, @PathVariable Long sectionId, @RequestBody SeatRequestDto newSeatDto) {
        return seatService.addSeat(venueId, sectionId, newSeatDto);
    }

    @PutMapping("/{seatId}")
    public Seat updateSeat(@PathVariable Long venueId, @PathVariable Long sectionId, @PathVariable Long seatId, @RequestBody SeatRequestDto newSeatDto) {
        return seatService.updateSeat(venueId, sectionId, seatId, newSeatDto);
    }

    @DeleteMapping("/{seatId}")
    @ResponseStatus(code=HttpStatus.NO_CONTENT)
    public void deleteSeat(@PathVariable Long venueId, @PathVariable Long sectionId, @PathVariable Long seatId) {
        seatService.deleteSeat(venueId, sectionId, seatId);
    }
}
