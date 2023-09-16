package com.cs203g3.ticketing.concertImage;

import java.io.IOException;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cs203g3.ticketing.concert.Concert;
import com.cs203g3.ticketing.concert.ConcertRepository;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ConcertImageService {
    private ConcertImageRepository concertImages;
    private ConcertRepository concerts;

    public ConcertImageService(ConcertImageRepository cir, ConcertRepository cr) {
        this.concertImages = cir;
        this.concerts = cr;
    }

    @Transactional
    public Stream<ConcertImage> getConcertImageByConcert(Long concertId) {
        Concert concert = concerts.findById(concertId)
            .orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return concertImages.findByConcert(concert);
    }

    public ConcertImage getConcertImageByConcertAndId(Long concertId, Long concertImageId) {
        Concert concert = concerts.findById(concertId)
            .orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return concertImages.findByConcertAndId(concert, concertImageId);
    }

    public ConcertImage addConcertImage(Long concertId, MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        ConcertImage newConcertImage = new ConcertImage(null, fileName, file.getBytes(), null);

        concerts.findById(concertId).map((concert) -> {
            newConcertImage.setConcert(concert);
            return concert;
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));

        return concertImages.save(newConcertImage);
    }

    public void deleteConcertImage(Long concertImage) {
        concertImages.deleteById(concertImage);
    }
}
