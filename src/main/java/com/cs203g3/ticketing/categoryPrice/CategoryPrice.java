package com.cs203g3.ticketing.categoryPrice;

import java.math.BigDecimal;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.categoryPrice.key.CategoryPriceKey;
import com.cs203g3.ticketing.concert.Concert;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@IdClass(CategoryPriceKey.class)
public class CategoryPrice {
    @Id
    @ManyToOne
    @JoinColumn(name="concertId")
    private Concert concert;

    @Id
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;

    @NotNull(message="Price must not be null")
    @DecimalMin(value="0.0", inclusive=true, message="Price must be greater than 0")
    @Digits(integer=6, fraction=2, message="Invalid price value (<6 digits>.<2 digits> expected)")
    private BigDecimal price;
}
