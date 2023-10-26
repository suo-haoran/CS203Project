package com.cs203g3.ticketing.category;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.category.dto.CategoryIdAndNameOnlyResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/venues/{venueId}/categories")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService cs) {
        this.categoryService = cs;
    }

    @GetMapping
    public List<CategoryIdAndNameOnlyResponseDto> getAllCategoriesByVenueId(@Valid @PathVariable Long venueId) {
        return categoryService.getAllCategoriesByVenueId(venueId);
    }
}
