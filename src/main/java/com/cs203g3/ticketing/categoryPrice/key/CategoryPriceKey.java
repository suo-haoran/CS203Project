package com.cs203g3.ticketing.categoryPrice.key;

import java.io.Serializable;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@EqualsAndHashCode
@Getter
@Setter
public class CategoryPriceKey implements Serializable{
    private Concert concert;
    private Category category;
}
