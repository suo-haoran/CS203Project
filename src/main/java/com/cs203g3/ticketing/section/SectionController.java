package com.cs203g3.ticketing.section;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.section.dto.SectionIdAndNameDto;
import com.cs203g3.ticketing.section.dto.SectionRequestDto;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/venues/{venueId}/sections")
public class SectionController {
    
    private SectionService sectionService;

    public SectionController(SectionService ss) {
        this.sectionService = ss;
    }

    @GetMapping
    public List<SectionIdAndNameDto> getAllSectionsByVenue(@PathVariable Long venueId) {
        return sectionService.getAllSectionsByVenueId(venueId);
    }

    @GetMapping("/{sectionId}")
    public SectionIdAndNameDto getSection(@PathVariable Long venueId, @PathVariable Long sectionId) {
        return sectionService.getSectionByVenueIdAndSectionId(venueId, sectionId);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Section addSection(@PathVariable Long venueId, @Valid @RequestBody SectionRequestDto newSectionDto) {
        return sectionService.addSectionByVenueId(venueId, newSectionDto);
    }

    @PutMapping("/{sectionId}")
    public Section updateSection(@PathVariable Long venueId, @PathVariable Long sectionId, @Valid @RequestBody SectionRequestDto newSectionDto) {
        return sectionService.updateSectionByVenueIdAndSectionId(venueId, sectionId, newSectionDto);
    }

    @DeleteMapping("/{sectionId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteSection(@PathVariable Long venueId, @PathVariable Long sectionId) {
        sectionService.deleteSectionByVenueIdAndSectionId(venueId, sectionId);
    }
}
