package com.cs203g3.ticketing.activeBallotCategory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.activeBallotCategory.key.ActiveBallotCategoryKey;

import jakarta.transaction.Transactional;

public interface ActiveBallotCategoryRepository extends JpaRepository<ActiveBallotCategory, ActiveBallotCategoryKey> {
    Optional<ActiveBallotCategory> findByConcertIdAndCategoryId(Long concertId, Long categoryId);

    @Transactional
    void deleteByConcertIdAndCategoryId(Long concertId, Long categoryId);
}
