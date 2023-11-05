package com.cs203g3.ticketing.activeBallotCategory;

import com.cs203g3.ticketing.activeBallotCategory.key.ActiveBallotCategoryKey;
import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.persistence.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @NotNull(message="status must not be null")
    private EnumActiveBallotCategoryStatus status;

    public ActiveBallotCategory(Concert concert, Category category) {
        this.concert = concert;
        this.category = category;
        this.status = EnumActiveBallotCategoryStatus.ACTIVE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;

        ActiveBallotCategory other = (ActiveBallotCategory) obj;

        if (concert == null && other.concert != null) {
            return false;
        } else if (!concert.equals(other.concert)) {
            return false;
        }

        if (category == null && other.category != null) {
            return false;
        } else if (!category.equals(other.category)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((concert == null) ? 0 : concert.hashCode());
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        return result;
    }
}
