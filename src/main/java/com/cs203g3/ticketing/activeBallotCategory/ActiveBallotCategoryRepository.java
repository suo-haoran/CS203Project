package com.cs203g3.ticketing.activeBallotCategory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.activeBallotCategory.key.ActiveBallotCategoryKey;

import jakarta.transaction.Transactional;

public interface ActiveBallotCategoryRepository extends JpaRepository<ActiveBallotCategory, ActiveBallotCategoryKey> {
    @Transactional
    void deleteByConcertIdAndCategoryId(Long concertId, Long categoryId);
}
