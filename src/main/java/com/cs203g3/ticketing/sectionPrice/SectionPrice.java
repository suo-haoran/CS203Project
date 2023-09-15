package com.cs203g3.ticketing.sectionPrice;

import java.math.BigDecimal;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.sectionPrice.key.SectionPriceKey;

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
@IdClass(SectionPriceKey.class)
public class SectionPrice {
    @Id
    @ManyToOne
    @JoinColumn(name="concertId")
    private Concert concert;

    @Id
    @ManyToOne
    @JoinColumn(name="sectionId")
    private Section section;

    @NotNull
    @DecimalMin(value="0.0", inclusive=true)
    @Digits(integer=6,fraction=2)
    private BigDecimal price;
}
