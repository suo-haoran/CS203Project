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
import com.cs203g3.ticketing.exception.ResourceAlreadyExistsException;
import com.cs203g3.ticketing.exception.ResourceNotFoundException;

@Service
public class ConcertImageService {
    private ConcertImageRepository concertImages;
    private ConcertRepository concerts;
    private static final String TARGET_DIR = "./images/";

    static {
        // Create the image directory if it doesn't exist.
        File file = new File(TARGET_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public ConcertImageService(ConcertImageRepository cir, ConcertRepository cr) {
        this.concertImages = cir;
        this.concerts = cr;
    }

    /**
     * Retrieves a stream of concert images associated with a specific concert.
     *
     * @param concertId The ID of the concert.
     * @return A stream of ConcertImage objects representing the images of the
     *         specified concert.
     * @throws ResourceNotFoundException If the specified concert does not exist.
     */
    @Transactional
    public Stream<ConcertImage> getConcertImageByConcert(Long concertId) {
        Concert concert = concerts.findById(concertId)
                .orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return concertImages.findByConcert(concert);
    }

    /**
     * Retrieves a specific concert image by its ID and the ID of the associated
     * concert.
     *
     * @param concertId      The ID of the concert.
     * @param concertImageId The ID of the concert image.
     * @return A ConcertImage object representing the requested image.
     * @throws ResourceNotFoundException If the specified concert or image does not
     *                                   exist.
     */
    public ConcertImage getConcertImageByConcertAndId(Long concertId, Long concertImageId) {
        Concert concert = concerts.findById(concertId)
                .orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));
        return concertImages.findByConcertAndId(concert, concertImageId);
    }

    /**
     * Adds a new concert image to the application and saves it to the file system.
     *
     * @param concertId The ID of the concert to associate the image with.
     * @param file      The MultipartFile containing the image data.
     * @return The newly added ConcertImage object.
     * @throws IOException               If there is an issue with file I/O.
     * @throws ResourceNotFoundException If the specified concert does not exist.
     */
    public ConcertImage addConcertImage(Long concertId, MultipartFile file) throws IOException {
        String fileName = StringUtils
                .cleanPath(file.getOriginalFilename() + "_" + new Date().toInstant().toEpochMilli());
        String filePath = writeImageToFileSystem(fileName, file.getBytes());
        ConcertImage newConcertImage = new ConcertImage(null, fileName, filePath, null);

        concerts.findById(concertId).map((concert) -> {
            newConcertImage.setConcert(concert);
            return concert;
        }).orElseThrow(() -> new ResourceNotFoundException(Concert.class, concertId));

        return concertImages.save(newConcertImage);
    }

     /**
     * Deletes a concert image by its ID and removes it from the file system.
     *
     * @param concertImageId The ID of the concert image to delete.
     * @throws ResourceNotFoundException If the specified image does not exist.
     */
    public void deleteConcertImage(Long concertImageId) {
        concertImages.findById(concertImageId).map(concertImage -> {
            File file = new File(concertImage.getFilePath());
            file.delete();
            return concertImage;
        }).orElseThrow(() -> new ResourceNotFoundException(ConcertImage.class, concertImageId));

        concertImages.deleteById(concertImageId);
    }

    /**
     * Reads an image from the file system by its file name.
     *
     * @param fileName The name of the image file to read.
     * @return A byte array representing the image data.
     * @throws IOException If there is an issue with file I/O or if the image file
     *                     is not found.
     */
    public byte[] readImageFromFileSystem(String fileName) throws IOException {
        File file = new File(TARGET_DIR + fileName);
        byte[] image = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(image);
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Image " + fileName + " not found");
        }
        return image;
    }

    /**
     * Writes an image to the file system with the specified file name and byte
     * data.
     *
     * @param fileName The name of the image file to write.
     * @param bytes    The byte array containing the image data.
     * @return The path of the written image file.
     * @throws IOException                    If there is an issue with file I/O or
     *                                        if the image file already exists.
     * @throws ResourceAlreadyExistsException If the image file already exists.
     */
    private String writeImageToFileSystem(String fileName, byte[] bytes) throws IOException {
        FileOutputStream fos;
        File file = new File(TARGET_DIR + fileName);

        if (file.exists()) {
            throw new ResourceAlreadyExistsException(fileName + " already exists");
        }
        fos = new FileOutputStream(file, false);
        try {
            file.createNewFile();
            fos.write(bytes);
        } finally {
            fos.close();
        }
        return file.getPath();
    }

}
