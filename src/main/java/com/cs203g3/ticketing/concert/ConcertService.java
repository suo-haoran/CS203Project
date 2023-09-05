package com.cs203g3.ticketing.concert;

import java.util.List;

public interface ConcertService {
    List<Concert> getAllConcerts();
    Concert getConcert(Long id);
    Concert addConcert(Concert concert);
    Concert updateConcert(Long id, Concert updatedConcert);
    void deleteConcert(Long id);
}
