package com.cs203g3.ticketing.concertImage;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.concert.Concert;

public interface ConcertImageRepository extends JpaRepository<ConcertImage, Long> {
    ConcertImage findByConcertAndId(Concert concert, Long id);

    Stream<ConcertImage> findByConcert(Concert concert);
}
