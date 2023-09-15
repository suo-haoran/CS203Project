package com.cs203g3.ticketing.sectionPrice.dto;

import java.math.BigDecimal;

import com.cs203g3.ticketing.section.dto.SectionIdAndNameDto;

import lombok.Data;

@Data
public class SectionPriceResponseDto {
    private SectionIdAndNameDto section;
    private BigDecimal price;
}
