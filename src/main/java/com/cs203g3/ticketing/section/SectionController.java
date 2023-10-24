package com.cs203g3.ticketing.section;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.section.dto.SectionResponseDto;



@RestController
@RequestMapping("/venues/{venueId}/sections")
public class SectionController {
    
    private SectionService sectionService;

    public SectionController(SectionService ss) {
        this.sectionService = ss;
    }

    @GetMapping
    public List<SectionResponseDto> getAllSectionsByVenue(@RequestParam(required=false) Long categoryId, @PathVariable Long venueId) {
        return categoryId != null
            ? sectionService.getAllSectionsByVenueIdAndCategoryId(venueId, categoryId)
            : sectionService.getAllSectionsByVenueId(venueId);
    }

    @GetMapping("/{sectionId}")
    public SectionResponseDto getSection(@PathVariable Long venueId, @PathVariable Long sectionId) {
        return sectionService.getSectionByVenueIdAndSectionId(venueId, sectionId);
    }
}
