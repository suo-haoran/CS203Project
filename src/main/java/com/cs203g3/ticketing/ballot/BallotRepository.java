package com.cs203g3.ticketing.ballot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BallotRepository extends JpaRepository<Ballot, Long> {
    List<Ballot> findAllByConcertSessionIdAndCategoryId(Long concertSessionId, Long categoryId);

    List<Ballot> findAllByConcertSessionIdAndCategoryIdAndPurchaseAllowedOrderByBallotResultAsc(
        Long concertSessionId, Long categoryId, EnumPurchaseAllowed purchaseAllowed);

    List<Ballot> findAllByConcertSessionIdAndCategoryIdAndPurchaseAllowedOrderByBallotResultAsc(
        Long concertSessionId, Long categoryId, EnumPurchaseAllowed purchaseAllowed, Pageable pageable);

    Integer countByConcertSessionConcertIdAndCategoryIdAndPurchaseAllowedIn(
        Long concertSessionId, Long categoryId, List<EnumPurchaseAllowed> purchaseAllowedSet
    );

    Optional<Ballot> findByConcertSessionIdAndCategoryIdAndUserId(Long concertSessionId, Long categoryId, Long userId);
}
