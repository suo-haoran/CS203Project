package com.cs203g3.ticketing.categoryPrice;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.cs203g3.ticketing.category.Category;
import com.cs203g3.ticketing.category.CategoryRepository;
import com.cs203g3.ticketing.categoryPrice.dto.CategoryPriceRequestDto;
import com.cs203g3.ticketing.categoryPrice.dto.CategoryPriceResponseDto;
import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceAlreadyExistsException;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class CategoryPriceService {

    private ModelMapper modelMapper;
    
    private CategoryPriceRepository categoryPrices;
    private ConcertRepository concerts;
    private CategoryRepository categories;

    public CategoryPriceService(ModelMapper modelMapper, CategoryPriceRepository categoryPrices, ConcertRepository concerts, CategoryRepository categories) {
        this.modelMapper = modelMapper;

        this.categoryPrices = categoryPrices;
        this.concerts = concerts;
        this.categories = categories;
    }

    public List<CategoryPriceResponseDto> getAllCategoryPricesByConcertId(Long concertId) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return categoryPrices.findByConcert(concert).stream()
            .map(price -> modelMapper.map(price, CategoryPriceResponseDto.class))
            .collect(Collectors.toList());
    }

    public CategoryPriceResponseDto getCategoryPriceByConcertIdAndCategoryId(Long concertId, Long categoryId) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        CategoryPrice price = categoryPrices.findByConcertAndCategory(concert, category).orElseThrow(() -> {
            throw new ResourceNotFoundException(String.format("Price cannot be found for Category #%d in Concert #%d", categoryId, concertId));
        });

        return modelMapper.map(price, CategoryPriceResponseDto.class);
    }

    public CategoryPriceResponseDto addCategoryPrice(Long concertId, Long categoryId, CategoryPriceRequestDto newCategoryPriceDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndId(concert.getVenue(), categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        categoryPrices.findByConcertAndCategory(concert, category).ifPresent(value -> {
            throw new ResourceAlreadyExistsException(String.format("Price for Concert #%d and Category #%d already exists", concertId, categoryId));
        });

        CategoryPrice newCategoryPrice = modelMapper.map(newCategoryPriceDto, CategoryPrice.class);
        newCategoryPrice.setConcert(concert);
        newCategoryPrice.setCategory(category);
        categoryPrices.save(newCategoryPrice);
        
        return modelMapper.map(newCategoryPrice, CategoryPriceResponseDto.class);
    }

    public CategoryPriceResponseDto updateCategoryPrice(Long concertId, Long categoryId, CategoryPriceRequestDto newCategoryPriceDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndId(concert.getVenue(), categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        CategoryPrice newCategoryPrice = modelMapper.map(newCategoryPriceDto, CategoryPrice.class);
        newCategoryPrice.setConcert(concert);
        newCategoryPrice.setCategory(category);
        categoryPrices.save(newCategoryPrice);
        
        return modelMapper.map(newCategoryPrice, CategoryPriceResponseDto.class);
    }

    public void deleteCategoryPrice(Long concertId, Long categoryId) {
        categoryPrices.deleteByConcertIdAndCategoryId(concertId, categoryId);
    }
}
