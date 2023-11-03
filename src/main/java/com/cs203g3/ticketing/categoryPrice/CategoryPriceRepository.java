package com.cs203g3.ticketing.categoryPrice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.categoryPrice.key.CategoryPriceKey;
import com.cs203g3.ticketing.concert.Concert;

import jakarta.transaction.Transactional;


public interface CategoryPriceRepository extends JpaRepository<CategoryPrice, CategoryPriceKey>{
    List<CategoryPrice> findByConcert(Concert concert);
    Optional<CategoryPrice> findByConcertAndCategory(Concert concert, Category category);

    @Transactional
    void deleteByConcertIdAndCategoryId(Long concertId, Long categoryId);
}
