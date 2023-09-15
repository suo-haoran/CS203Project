package com.cs203g3.ticketing.sectionPrice;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceAlreadyExistsException;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.section.Section;
import com.cs203g3.ticketing.section.SectionRepository;
import com.cs203g3.ticketing.sectionPrice.dto.SectionPriceRequestDto;
import com.cs203g3.ticketing.sectionPrice.dto.SectionPriceResponseDto;

@Service
public class SectionPriceService {

    @Autowired
    private ModelMapper modelMapper;
    
    private SectionPriceRepository sectionPrices;
    private ConcertRepository concerts;
    private SectionRepository sections;

    public SectionPriceService(SectionPriceRepository sectionPrices, ConcertRepository concerts, SectionRepository sections) {
        this.sectionPrices = sectionPrices;
        this.concerts = concerts;
        this.sections = sections;
    }

    public List<SectionPriceResponseDto> getSectionPriceByConcert(Long concertId) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return sectionPrices.findByConcert(concert).stream()
            .map(price -> modelMapper.map(price, SectionPriceResponseDto.class))
            .collect(Collectors.toList());
    } 

    public SectionPriceResponseDto addSectionPrice(Long concertId, Long sectionId, SectionPriceRequestDto newSectionPriceDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Section section = sections.findByVenueAndId(concert.getVenue(), sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));
        sectionPrices.findByConcertAndSection(concert, section).ifPresent(value -> {
            throw new ResourceAlreadyExistsException(String.format("SectionPrice with ConcertId #%d and SectionId #%d already exists", concertId, sectionId));
        });

        SectionPrice newSectionPrice = modelMapper.map(newSectionPriceDto, SectionPrice.class);
        newSectionPrice.setConcert(concert);
        newSectionPrice.setSection(section);
        sectionPrices.save(newSectionPrice);
        
        return modelMapper.map(newSectionPrice, SectionPriceResponseDto.class);
    }

    public SectionPriceResponseDto updateSectionPrice(Long concertId, Long sectionId, SectionPriceRequestDto newSectionPriceDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Section section = sections.findByVenueAndId(concert.getVenue(), sectionId).orElseThrow(() -> new ResourceNotFoundException(Section.class, sectionId));

        SectionPrice newSectionPrice = modelMapper.map(newSectionPriceDto, SectionPrice.class);
        newSectionPrice.setConcert(concert);
        newSectionPrice.setSection(section);
        sectionPrices.save(newSectionPrice);
        
        return modelMapper.map(newSectionPrice, SectionPriceResponseDto.class);
    }

    public void deleteSectionPrice(Long concertId, Long sectionId) {
        sectionPrices.deleteByConcertIdAndSectionId(concertId, sectionId);
    }
}
