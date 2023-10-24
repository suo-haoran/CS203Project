package com.cs203g3.ticketing.concertImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ConcertImageServiceTest {

    @InjectMocks
    private ConcertImageService concertImageService;

    @Mock
    private ConcertImageRepository concertImageRepository;

    @Mock
    private ConcertRepository concertRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getConcertImageByConcert_Success() {
        // Mock data and behavior for concertRepository and concertImageRepository
        Long concertId = 1L;
        Concert concert = new Concert();
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        Stream<ConcertImage> mockImages = Stream.of(new ConcertImage(1L, "image1.jpg", "/path/to/image1.jpg", concert));
        when(concertImageRepository.findByConcert(concert)).thenReturn(mockImages);

        // Call the service method
        Stream<ConcertImage> result = concertImageService.getConcertImageByConcert(concertId);

        // Assertions
        assertNotNull(result);
        assertEquals(mockImages, result);
        verify(concertRepository).findById(concertId);
        verify(concertImageRepository).findByConcert(concert);
    }
    
    @Test
    public void getConcertImageByConcert_Failure() {
        // Mock data and behavior for concertRepository and concertImageRepository
        Long concertId = 1L;

        when(concertRepository.findById(concertId)).thenThrow(ResourceNotFoundException.class);

        assertThrowsExactly(ResourceNotFoundException.class,() -> {
            concertImageService.getConcertImageByConcert(concertId);
        });

        verify(concertRepository).findById(concertId);
    }

    @Test
    public void addConcertImage_Success() throws IOException {
        // Mock data and behavior for concertRepository and concertImageRepository
        Long concertId = 1L;
        Concert concert = new Concert();
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));

        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("image1.jpg");
        when(mockFile.getBytes()).thenReturn(new byte[0]);

        // Mock saving an image
        ConcertImage savedImage = new ConcertImage(1L, "image1.jpg", "/path/to/image1.jpg", concert);
        when(concertImageRepository.save(any(ConcertImage.class))).thenReturn(savedImage);

        // Call the service method
        ConcertImage result = concertImageService.addConcertImage(concertId, mockFile);

        // Assertions
        assertNotNull(result);
        assertEquals(savedImage, result);
        verify(concertRepository).findById(concertId);
        verify(concertImageRepository).save(any(ConcertImage.class));
    }
}
