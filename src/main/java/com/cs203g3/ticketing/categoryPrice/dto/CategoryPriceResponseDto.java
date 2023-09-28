package com.cs203g3.ticketing.categoryPrice.dto;

import java.math.BigDecimal;

import com.cs203g3.ticketing.category.Category;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryPriceResponseDto {
    private Category category;

    @NotNull(message="Price must not be null")
    @DecimalMin(value="0.0", inclusive=true, message="Price must be greater than 0")
    @Digits(integer=6, fraction=2, message="Invalid price value (<6 digits>.<2 digits> expected)")
    private BigDecimal price;
}
