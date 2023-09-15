package com.cs203g3.ticketing.sectionPrice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.section.Section;

import jakarta.transaction.Transactional;


public interface SectionPriceRepository extends JpaRepository<SectionPrice, Long>{
    List<SectionPrice> findByConcert(Concert concert);
    Optional<SectionPrice> findByConcertAndSection(Concert concert, Section section);

    @Transactional
    void deleteByConcertIdAndSectionId(Long concertId, Long sectionId);
}
