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

     /**
     * Retrieves a list of category prices for a given concert by its ID.
     *
     * @param concertId The ID of the concert.
     * @return A list of CategoryPriceResponseDto objects representing the category prices for the concert.
     * @throws ResourceNotFoundException If the specified concert does not exist.
     */
    public List<CategoryPriceResponseDto> getAllCategoryPricesByConcertId(Long concertId) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return categoryPrices.findByConcert(concert).stream()
            .map(price -> modelMapper.map(price, CategoryPriceResponseDto.class))
            .collect(Collectors.toList());
    }

     /**
     * Retrieves the category price for a specific concert and category by their IDs.
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     * @return A CategoryPriceResponseDto representing the category price.
     * @throws ResourceNotFoundException If the specified concert or category does not exist, or if the price is not found.
     */
    public CategoryPriceResponseDto getCategoryPriceByConcertIdAndCategoryId(Long concertId, Long categoryId) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        CategoryPrice price = categoryPrices.findByConcertAndCategory(concert, category).orElseThrow(() -> {
            throw new ResourceNotFoundException(String.format("Price cannot be found for Category #%d in Concert #%d", categoryId, concertId));
        });

        return modelMapper.map(price, CategoryPriceResponseDto.class);
    }

    /**
     * Adds a new category price for a concert and category.
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     * @param newCategoryPriceDto The CategoryPriceRequestDto with the new category price information.
     * @return A CategoryPriceResponseDto representing the newly added category price.
     * @throws ResourceNotFoundException If the specified concert or category does not exist.
     * @throws ResourceAlreadyExistsException If a price already exists for the given concert and category.
     */
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

      /**
     * Updates the category price for a concert and category.
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     * @param newCategoryPriceDto The CategoryPriceRequestDto with the updated category price information.
     * @return A CategoryPriceResponseDto representing the updated category price.
     * @throws ResourceNotFoundException If the specified concert or category does not exist.
     */
    public CategoryPriceResponseDto updateCategoryPrice(Long concertId, Long categoryId, CategoryPriceRequestDto newCategoryPriceDto) {
        Concert concert = concerts.findById(concertId).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        Category category = categories.findByVenueAndId(concert.getVenue(), categoryId).orElseThrow(() -> new ResourceNotFoundException(Category.class, categoryId));

        CategoryPrice newCategoryPrice = modelMapper.map(newCategoryPriceDto, CategoryPrice.class);
        newCategoryPrice.setConcert(concert);
        newCategoryPrice.setCategory(category);
        categoryPrices.save(newCategoryPrice);
        
        return modelMapper.map(newCategoryPrice, CategoryPriceResponseDto.class);
    }

    /**
     * Deletes the category price for a concert and category.
     *
     * @param concertId The ID of the concert.
     * @param categoryId The ID of the category.
     */
    public void deleteCategoryPrice(Long concertId, Long categoryId) {
        categoryPrices.deleteByConcertIdAndCategoryId(concertId, categoryId);
    }
}
