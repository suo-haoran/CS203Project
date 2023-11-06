package com.cs203g3.ticketing.activeBallotCategory;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cs203g3.ticketing.activeBallotCategory.key.ActiveBallotCategoryKey;


public interface ActiveBallotCategoryRepository extends JpaRepository<ActiveBallotCategory, ActiveBallotCategoryKey> {
    Optional<ActiveBallotCategory> findByConcertIdAndCategoryId(Long concertId, Long categoryId);
    Optional<ActiveBallotCategory> findByConcertIdAndCategoryIdAndStatusIn(Long concertId, Long categoryId, List<EnumActiveBallotCategoryStatus> statusList);
    Optional<ActiveBallotCategory> findByConcertIdAndCategoryIdAndStatusNotIn(Long concertId, Long categoryId, List<EnumActiveBallotCategoryStatus> statusList);
}
