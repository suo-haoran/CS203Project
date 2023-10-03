package com.cs203g3.ticketing.ballot;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.persistence.BaseEntity;
import com.cs203g3.ticketing.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueUserConcertCategoryIdentifier", columnNames = { "userId", "concertId", "categoryId" })
})
@EqualsAndHashCode(callSuper=true)
public class Ballot extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="purchaseAllowed must not be null")
    private boolean purchaseAllowed;

    // Nullable, will only be populated once the ballot is randomized
    private Long ballotResult;

    @NotNull(message="User must not be null")
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @NotNull(message="Concert must not be null")
    @ManyToOne
    @JoinColumn(name="concertId")
    private Concert concert;

    @NotNull(message="Category must not be null")
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;

    public Ballot(User user, Concert concert, Category category) {
        this.user = user;
        this.concert = concert;
        this.category = category;
        this.purchaseAllowed = false;
    }
}
