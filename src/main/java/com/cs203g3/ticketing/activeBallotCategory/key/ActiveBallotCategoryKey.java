package com.cs203g3.ticketing.activeBallotCategory.key;

import java.io.Serializable;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode
public class ActiveBallotCategoryKey implements Serializable{
    private Concert concert;
    private Category category;
}
