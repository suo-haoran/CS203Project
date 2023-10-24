package com.cs203g3.ticketing.concert;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    List<Concert> findAllBySessionsNotNull();
}
