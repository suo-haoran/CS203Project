package com.cs203g3.ticketing.activeBallotCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.activeBallotCategory.key.ActiveBallotCategoryKey;


public interface ActiveBallotCategoryRepository extends JpaRepository<ActiveBallotCategory, ActiveBallotCategoryKey> {
    Optional<ActiveBallotCategory> findByConcertIdAndCategoryIdAndStatus(Long concertId, Long categoryId, EnumActiveBallotCategoryStatus status);
}
