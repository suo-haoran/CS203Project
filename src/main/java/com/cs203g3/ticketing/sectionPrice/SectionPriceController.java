package com.cs203g3.ticketing.sectionPrice;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.sectionPrice.dto.SectionPriceRequestDto;
import com.cs203g3.ticketing.sectionPrice.dto.SectionPriceResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/concerts/{concertId}")
public class SectionPriceController {
    private SectionPriceService sectionPriceService;

    public SectionPriceController(SectionPriceService sps) {
        this.sectionPriceService = sps;
    }

    @GetMapping("/prices")
    public List<SectionPriceResponseDto> getAllSectionPricesByConcert(@PathVariable Long concertId) {
        return sectionPriceService.getSectionPriceByConcert(concertId);
    }

    @PostMapping("/section/{sectionId}/prices")
    public SectionPriceResponseDto addSectionPrice(@PathVariable Long concertId, @PathVariable Long sectionId, @Valid @RequestBody SectionPriceRequestDto newSectionPriceDto) {
        return sectionPriceService.addSectionPrice(concertId, sectionId, newSectionPriceDto);
    }

    @PutMapping("/section/{sectionId}/prices")
    public SectionPriceResponseDto updateSectionPrice(@PathVariable Long concertId, @PathVariable Long sectionId, @Valid @RequestBody SectionPriceRequestDto newSectionPriceDto) {
        return sectionPriceService.updateSectionPrice(concertId, sectionId, newSectionPriceDto);
    }

    @DeleteMapping("/section/{sectionId}/prices")
    public void deleteSectionPrice(@PathVariable Long concertId, @PathVariable Long sectionId) {
        sectionPriceService.deleteSectionPrice(concertId, sectionId);
    }
}
