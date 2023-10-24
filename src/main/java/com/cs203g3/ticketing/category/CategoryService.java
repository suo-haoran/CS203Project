package com.cs203g3.ticketing.category;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.cs203g3.ticketing.category.dto.CategoryIdAndNameOnlyResponseDto;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@Service
public class CategoryService {

    private ModelMapper modelMapper;
    private CategoryRepository categories;
    private VenueRepository venues;

    public CategoryService(ModelMapper modelMapper, CategoryRepository categories, VenueRepository venues) {
        this.modelMapper = modelMapper;
        this.categories = categories;
        this.venues = venues;
    }

    public List<CategoryIdAndNameOnlyResponseDto> getAllCategoriesByVenueId(@PathVariable Long venueId) {
        Venue venue = venues.findById(venueId).orElseThrow(() -> new ResourceNotFoundException(Venue.class, venueId));
        return categories.findAllByVenue(venue).stream()
            .map(category -> modelMapper.map(category, CategoryIdAndNameOnlyResponseDto.class))
            .collect(Collectors.toList());
    }
}
