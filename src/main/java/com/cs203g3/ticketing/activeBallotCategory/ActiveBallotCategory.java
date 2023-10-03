package com.cs203g3.ticketing.activeBallotCategory;

import com.cs203g3.ticketing.activeBallotCategory.key.ActiveBallotCategoryKey;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.persistence.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
@IdClass(ActiveBallotCategoryKey.class)
public class ActiveBallotCategory extends BaseEntity {
    @Id
    @ManyToOne
    @JoinColumn(name="concertId")
    private Concert concert;

    @Id
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;

    public ActiveBallotCategory(Concert concert, Category category) {
        this.concert = concert;
        this.category = category;
    }
}
