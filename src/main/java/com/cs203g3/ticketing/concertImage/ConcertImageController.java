package com.cs203g3.ticketing.concertImage;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/v1/concerts/{concertId}/images")
public class ConcertImageController {

    private ConcertImageService concertImageService;

    public ConcertImageController(ConcertImageService cis) {
        this.concertImageService = cis;
    }

    /**
     * Retrieves a list of file download URIs for all images associated with a specific concert.
     *
     * @param concertId The ID of the concert for which to retrieve images.
     * @return A list of String values representing the download URIs for the images.
     */
    @GetMapping
    public List<String> getFileList(@PathVariable Long concertId) {
        return concertImageService.getConcertImageByConcert(concertId)
                .map(concertImage -> {
                    String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/v1/concerts/" + concertId + "/images/")
                            .path(concertImage.getId().toString())
                            .toUriString();
                    return fileDownloadUri;
                }).toList();
    }

    @GetMapping("/{concertImageId}")
    public ResponseEntity<byte[]> getConcertImage(@PathVariable Long concertId, @PathVariable Long concertImageId)
            throws IOException {

        ConcertImage concertImage = concertImageService.getConcertImageByConcertAndId(concertId, concertImageId);
        byte[] image = concertImageService.readImageFromFileSystem(concertImage.getName());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);

    }

    @PostMapping
    public ResponseEntity<?> addConcertImage(@PathVariable Long concertId,
            @RequestParam("file") MultipartFile multipartFile) throws IOException {
        ConcertImage image = concertImageService.addConcertImage(concertId, multipartFile);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/v1/concerts/" + concertId + "/images/" + image.getId());
        return new ResponseEntity<String>("", headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{concertImageId}")
    public void deleteConcertImage(@PathVariable Long concertImageId) {
        concertImageService.deleteConcertImage(concertImageId);
    }
}
