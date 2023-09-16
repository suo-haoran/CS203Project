package com.cs203g3.ticketing.concertImage;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpHeaders;
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
@RequestMapping("/api/concert/{concertId}/images")
public class ConcertImageController {

    private ConcertImageService concertImageService;

    public ConcertImageController(ConcertImageService cis) {
        this.concertImageService = cis;
    }

    @GetMapping
    public List<String> getFileList(@PathVariable Long concertId) {
        return concertImageService.getConcertImageByConcert(concertId)
            .map(concertImage -> {
                String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/concert/" + concertId + "/images/")
                    .path(concertImage.getId().toString())
                    .toUriString();
                return fileDownloadUri;
            }).toList();
    }

    @GetMapping("/{concertImageId}")
    public ResponseEntity<byte[]> getConcertImage(@PathVariable Long concertId, @PathVariable Long concertImageId) {
        ConcertImage concertImage = concertImageService.getConcertImageByConcertAndId(concertId, concertImageId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + concertImage.getName() + "\"").body(concertImage.getImage());
    }

    @PostMapping
    public String addConcertImage(@PathVariable Long concertId, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        try {
            concertImageService.addConcertImage(concertId, multipartFile);
            return "Uploaded " + multipartFile.getOriginalFilename() + " Successfully";
        } catch (IOException e) {
            return "Could not upload the file: " + multipartFile.getOriginalFilename() + "!";
        }
    }

    @DeleteMapping("/{concertImageId}")
    public void deleteConcertImage(Long concertImageId) {
        concertImageService.deleteConcertImage(concertImageId);
    }
}
