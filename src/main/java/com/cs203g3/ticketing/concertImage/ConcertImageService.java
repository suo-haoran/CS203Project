package com.cs203g3.ticketing.concertImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
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
    private static final String TARGET_DIR = "./images/";

    static {
        File file = new File(TARGET_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

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
        String fileName = StringUtils.cleanPath(file.getOriginalFilename() + "_" + new Date().toInstant().toEpochMilli());
        String filePath = writeImageToFileSystem(fileName, file.getBytes());
        ConcertImage newConcertImage = new ConcertImage(null, fileName, filePath, null);

        concerts.findById(concertId).map((concert) -> {
            newConcertImage.setConcert(concert);
            return concert;
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));

        return concertImages.save(newConcertImage);
    }

    public void deleteConcertImage(Long concertImageId) {
        concertImages.findById(concertImageId).map(concertImage -> {
            File file = new File(concertImage.getFilePath());
            file.delete();
            return concertImage;
        }).orElseThrow(() -> new ResourceNotFoundException(ConcertImage.class, concertImageId));
        
        concertImages.deleteById(concertImageId);
    }

    public byte[] readImageFromFileSystem(String fileName) throws IOException {
        File file = new File(TARGET_DIR + fileName);
        byte[] image = new byte[(int)file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(image);
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Image " + fileName + " not found");
        }
        return image;
    }

    private String writeImageToFileSystem(String fileName, byte[] bytes) throws IOException {
        FileOutputStream fos;
        File file = new File(TARGET_DIR + fileName);
        file.createNewFile();
        fos = new FileOutputStream(file, false);
        try {
            fos.write(bytes);
        } finally {
            fos.close();
        }
        return file.getPath();
    }

}
