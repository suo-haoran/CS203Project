package com.cs203g3.ticketing.ballot;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BallotRepository extends JpaRepository<Ballot, Long> {
    List<Ballot> findAllByConcertSessionIdAndCategoryId(Long concertSessionId, Long categoryId);
}
