package com.cs203g3.ticketing.ballot;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BallotRepository extends JpaRepository<Ballot, Long> {

}
