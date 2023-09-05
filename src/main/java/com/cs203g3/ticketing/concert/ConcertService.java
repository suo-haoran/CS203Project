package com.cs203g3.ticketing.concert;

import java.util.List;

import com.cs203g3.ticketing.concert.dto.ConcertRequestDto;

public interface ConcertService {
    List<Concert> getAllConcerts();
    Concert getConcert(Long id);
    Concert addConcert(ConcertRequestDto concertRequestDto);
    Concert updateConcert(Long id, ConcertRequestDto concertRequestDto);
    void deleteConcert(Long id);
}
