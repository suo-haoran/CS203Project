package com.cs203g3.ticketing.sectionPrice.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SectionPriceRequestDto {
    private BigDecimal price;
}
