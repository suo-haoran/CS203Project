package com.cs203g3.ticketing.categoryPrice;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.categoryPrice.dto.CategoryPriceRequestDto;
import com.cs203g3.ticketing.categoryPrice.dto.CategoryPriceResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/concerts/{concertId}")
public class CategoryPriceController {
    private CategoryPriceService categoryPriceService;

    public CategoryPriceController(CategoryPriceService cps) {
        this.categoryPriceService = cps;
    }

    @GetMapping("/prices")
    public List<CategoryPriceResponseDto> getAllCategoryPricesByConcertId(@PathVariable Long concertId) {
        return categoryPriceService.getAllCategoryPricesByConcertId(concertId);
    }

    @GetMapping("/categories/{categoryId}/prices")
    public CategoryPriceResponseDto getCategoryPriceByConcertIdAndCategoryId(@PathVariable Long concertId, @PathVariable Long categoryId) {
        return categoryPriceService.getCategoryPriceByConcertIdAndCategoryId(concertId, categoryId);
    }

    @PostMapping("/categories/{categoryId}/prices")
    public CategoryPriceResponseDto addCategoryPrice(@PathVariable Long concertId, @PathVariable Long categoryId, @Valid @RequestBody CategoryPriceRequestDto newCategoryPriceDto) {
        return categoryPriceService.addCategoryPrice(concertId, categoryId, newCategoryPriceDto);
    }

    @PutMapping("/categories/{categoryId}/prices")
    public CategoryPriceResponseDto updateCategoryPrice(@PathVariable Long concertId, @PathVariable Long categoryId, @Valid @RequestBody CategoryPriceRequestDto newCategoryPriceDto) {
        return categoryPriceService.updateCategoryPrice(concertId, categoryId, newCategoryPriceDto);
    }

    @DeleteMapping("/categories/{categoryId}/prices")
    public void deleteCategoryPrice(@PathVariable Long concertId, @PathVariable Long categoryId) {
        categoryPriceService.deleteCategoryPrice(concertId, categoryId);
    }
}
