package com.cs203g3.ticketing.concert;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.concert.dto.ConcertRequestDto;

@RestController
@RequestMapping("/concerts")
public class ConcertController {

    private ConcertService concertService;

    public ConcertController(ConcertService cs) {
        this.concertService = cs;
    }

    @GetMapping
    public List<Concert> getAllConcerts() {
        return concertService.getAllConcerts();
    }

    @GetMapping("/{id}")
    public Concert getConcert(Long id) {
        return concertService.getConcert(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Concert addConcert(@RequestBody ConcertRequestDto concertRequestDto) {
        return concertService.addConcert(concertRequestDto);
    }

    @PutMapping("/{id}")
    public Concert updateConcert(@PathVariable Long id, @RequestBody ConcertRequestDto concertRequestDto) {
        return concertService.updateConcert(id, concertRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteConcert(@PathVariable Long id) {
        concertService.deleteConcert(id);
    }
}
