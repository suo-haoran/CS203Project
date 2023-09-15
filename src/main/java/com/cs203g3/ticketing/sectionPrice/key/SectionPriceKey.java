package com.cs203g3.ticketing.sectionPrice.key;

import java.io.Serializable;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.section.Section;

import lombok.EqualsAndHashCode;


@EqualsAndHashCode
public class SectionPriceKey implements Serializable{
    private Concert concert;
    private Section section;
}
