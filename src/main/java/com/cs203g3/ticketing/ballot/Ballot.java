package com.cs203g3.ticketing.ballot;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.concertSession.ConcertSession;
import com.cs203g3.ticketing.persistence.BaseEntity;
import com.cs203g3.ticketing.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueUserConcertCategoryIdentifier", columnNames = { "userId", "concertSessionId", "categoryId" })
})
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class Ballot extends BaseEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message="purchaseAllowed must not be null")
    private EnumPurchaseAllowed purchaseAllowed;

    // Nullable, will only be populated once the ballot is randomized
    private Long ballotResult;

    @NotNull(message="User must not be null")
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @NotNull(message="ConcertSession must not be null")
    @ManyToOne
    @JoinColumn(name="concertSessionId")
    private ConcertSession concertSession;

    @NotNull(message="Category must not be null")
    @ManyToOne
    @JoinColumn(name="categoryId")
    private Category category;

    public Ballot(User user, ConcertSession concertSession, Category category) {
        this.user = user;
        this.concertSession = concertSession;
        this.category = category;
        this.purchaseAllowed = EnumPurchaseAllowed.NOT_YET;
    }
}
