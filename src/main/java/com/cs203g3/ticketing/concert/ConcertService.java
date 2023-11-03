package com.cs203g3.ticketing.concert;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.dto.ConcertRequestDto;
import com.cs203g3.ticketing.concert.dto.ConcertResponseDto;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class ConcertService {

    private ModelMapper modelMapper;

    private ConcertRepository concerts;
    private VenueRepository venues;

    public ConcertService(ModelMapper modelMapper, ConcertRepository concerts, VenueRepository venues) {
        this.modelMapper = modelMapper;

        this.concerts = concerts;
        this.venues = venues;
    }

    /**
     * Retrieves a list of all concerts available in the application.
     *
     * @return A list of ConcertResponseDto objects representing all concerts.
     */
    public List<ConcertResponseDto> getAllConcerts() {
        return concerts.findAll().stream()
                .map(concert -> modelMapper.map(concert, ConcertResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of concerts with non-null sessions, indicating concerts with scheduled sessions.
     *
     * @return A list of ConcertResponseDto objects representing concerts with scheduled sessions.
     */
    public List<ConcertResponseDto> getAllConcertsBySessionsNotNull() {
        return concerts.findAllBySessionsNotNull().stream()
                .map(concert -> modelMapper.map(concert, ConcertResponseDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a specific concert by its ID.
     *
     * @param id The ID of the concert to retrieve.
     * @return A ConcertResponseDto representing the requested concert.
     * @throws ResourceNotFoundException If the specified concert does not exist.
     */
    public ConcertResponseDto getConcert(Long id) {
        return concerts.findById(id).map(concert -> {
            return modelMapper.map(concert, ConcertResponseDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, id));
    }

    /**
     * Adds a new concert to the application.
     *
     * @param concertRequestDto The ConcertRequestDto containing the details of the new concert.
     * @return The newly added Concert object.
     * @throws ResourceNotFoundException If the specified venue for the concert does not exist.
     */
    public Concert addConcert(ConcertRequestDto concertRequestDto) {
        Long venueId = concertRequestDto.getVenueId();
        Concert newConcert = modelMapper.map(concertRequestDto, Concert.class);

        return venues.findById(venueId).map(venue -> {
            newConcert.setVenue(venue);
            return concerts.save(newConcert);
        }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
    }

    /**
     * Updates an existing concert's information.
     *
     * @param id The ID of the concert to update.
     * @param concertRequestDto The ConcertRequestDto with the updated concert details.
     * @return The updated Concert object.
     * @throws ResourceNotFoundException If the specified concert or venue does not exist.
     */
    public Concert updateConcert(Long id, ConcertRequestDto concertRequestDto) {
        Long venueId = concertRequestDto.getVenueId();
        Concert newConcert = modelMapper.map(concertRequestDto, Concert.class);

        return concerts.findById(id).map(concert -> {
            return venues.findById(venueId).map(venue -> {
                newConcert.setId(id);
                newConcert.setVenue(venue);
                return concerts.save(newConcert);
            }).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, id));
    }

    /**
     * Deletes a concert from the application by its ID.
     *
     * @param id The ID of the concert to delete.
     */
    public void deleteConcert(Long id) {
        concerts.deleteById(id);
    }
}