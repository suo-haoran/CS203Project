package com.cs203g3.ticketing.concert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.cs203g3.ticketing.concert.dto.ConcertRequestDto;
import com.cs203g3.ticketing.concert.dto.ConcertResponseDto;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;
import com.cs203g3.ticketing.venue.Venue;
import com.cs203g3.ticketing.venue.VenueRepository;

@ExtendWith(MockitoExtension.class)
public class ConcertServiceTest {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ConcertRepository concerts;

    @Mock
    private VenueRepository venues;

    @InjectMocks
    private ConcertService concertService;

    @Test
    public void getAllConcerts_Valid_ReturnConcertResponseDtoList() {
        Concert concert = new Concert();
        List<Concert> concertList = new ArrayList<Concert>();
        concertList.add(concert);
        concertList.add(concert);

        ConcertResponseDto concertDto = new ConcertResponseDto();
        List<ConcertResponseDto> concertDtoList = new ArrayList<ConcertResponseDto>();
        concertDtoList.add(concertDto);
        concertDtoList.add(concertDto);

        when(concerts.findAll()).thenReturn(concertList);
        when(modelMapper.map(any(Concert.class), eq(ConcertResponseDto.class))).thenReturn(concertDto);

        List<ConcertResponseDto> result = concertService.getAllConcerts();

        assertEquals(result, concertDtoList);
        verify(concerts).findAll();
        verify(modelMapper, times(2)).map(concert, ConcertResponseDto.class);
    }

    @Test
    public void getConcerts_Valid_ReturnConcertResponseDto() {
        Long concertId = 1L;
        Concert concert = new Concert();
        ConcertResponseDto concertDto = new ConcertResponseDto();

        when(concerts.findById(any(Long.class))).thenReturn(Optional.of(concert));
        when(modelMapper.map(any(Concert.class), eq(ConcertResponseDto.class))).thenReturn(concertDto);

        ConcertResponseDto result = concertService.getConcert(concertId);

        assertEquals(result, concertDto);
        verify(concerts).findById(concertId);
        verify(modelMapper).map(concert, ConcertResponseDto.class);
    }

    @Test
    public void addConcert_Valid_ReturnNewConcert() {
        ConcertRequestDto concertDto = new ConcertRequestDto();
        concertDto.setVenueId(1L);

        Concert concert = new Concert();

        when(modelMapper.map(any(ConcertRequestDto.class), eq(Concert.class))).thenReturn(concert);
        when(venues.findById(any(Long.class))).thenReturn(Optional.of(new Venue()));
        when(concerts.save(any(Concert.class))).thenReturn(concert);

        Concert newConcert = concertService.addConcert(concertDto);

        assertEquals(newConcert, concert);
        verify(modelMapper).map(concertDto, Concert.class);
        verify(venues).findById(concertDto.getVenueId());
        verify(concerts).save(concert);
    }

    @Test
    public void addConcert_VenueNotExist_ThrowResourceNotFound() {
        ConcertRequestDto concertDto = new ConcertRequestDto();
        concertDto.setVenueId(1L);

        Concert concert = new Concert();

        when(modelMapper.map(any(ConcertRequestDto.class), eq(Concert.class))).thenReturn(concert);
        when(venues.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            concertService.addConcert(concertDto);
        });
        verify(modelMapper).map(concertDto, Concert.class);
        verify(venues).findById(concertDto.getVenueId());
    }

    @Test
    public void updateConcert_Valid_ReturnNewConcert() {
        Long concertId = 1L;
        ConcertRequestDto concertDto = new ConcertRequestDto();
        concertDto.setVenueId(1L);

        Concert concert = new Concert();

        when(modelMapper.map(any(ConcertRequestDto.class), eq(Concert.class))).thenReturn(concert);
        when(concerts.findById(any(Long.class))).thenReturn(Optional.of(new Concert()));
        when(venues.findById(any(Long.class))).thenReturn(Optional.of(new Venue()));
        when(concerts.save(any(Concert.class))).thenReturn(concert);

        Concert newConcert = concertService.updateConcert(concertId, concertDto);

        assertEquals(newConcert, concert);
        verify(modelMapper).map(concertDto, Concert.class);
        verify(concerts).findById(concertId);
        verify(venues).findById(concertDto.getVenueId());
        verify(concerts).save(concert);
    }

    @Test
    public void updateConcert_ConcertNotExist_ReturnNewConcert() {
        Long concertId = 1L;
        ConcertRequestDto concertDto = new ConcertRequestDto();
        concertDto.setVenueId(1L);

        Concert concert = new Concert();

        when(modelMapper.map(any(ConcertRequestDto.class), eq(Concert.class))).thenReturn(concert);
        when(concerts.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            concertService.updateConcert(concertId, concertDto);
        });
        verify(modelMapper).map(concertDto, Concert.class);
        verify(concerts).findById(concertId);
    }

    @Test
    public void updateConcert_VenueNotExist_ReturnNewConcert() {
        Long concertId = 1L;
        ConcertRequestDto concertDto = new ConcertRequestDto();
        concertDto.setVenueId(1L);

        Concert concert = new Concert();

        when(modelMapper.map(any(ConcertRequestDto.class), eq(Concert.class))).thenReturn(concert);
        when(concerts.findById(any(Long.class))).thenReturn(Optional.of(new Concert()));
        when(venues.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> {
            concertService.updateConcert(concertId, concertDto);
        });

        verify(modelMapper).map(concertDto, Concert.class);
        verify(concerts).findById(concertId);
        verify(venues).findById(concertDto.getVenueId());
    }

    @Test
    public void deleteConcert_Valid_ReturnVoid() {
        Long concertId = 1L;

        concertService.deleteConcert(concertId);
        verify(concerts).deleteById(concertId);
    }
}
