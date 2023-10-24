package com.cs203g3.ticketing.concert;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.concert.dto.ConcertRequestDto;
import com.cs203g3.ticketing.concert.dto.ConcertResponseDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/concerts")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConcertController {

    private ConcertService concertService;

    public ConcertController(ConcertService cs) {
        this.concertService = cs;
    }

    @GetMapping
    public List<ConcertResponseDto> getAllConcerts(@RequestParam(required=false) boolean showAll) {
        // showAll param can only be used by Admins
        // will display all Concerts, even those with no related ConcertSessions
        return showAll
            ? concertService.getAllConcerts()
            : concertService.getAllConcertsBySessionsNotNull();
    }

    @GetMapping("/{id}")
    public ConcertResponseDto getConcert(@PathVariable Long id) {
        return concertService.getConcert(id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Concert addConcert(@Valid @RequestBody ConcertRequestDto concertRequestDto) {
        return concertService.addConcert(concertRequestDto);
    }

    @PutMapping("/{id}")
    public Concert updateConcert(@PathVariable Long id, @Valid @RequestBody ConcertRequestDto concertRequestDto) {
        return concertService.updateConcert(id, concertRequestDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteConcert(@PathVariable Long id) {
        concertService.deleteConcert(id);
    }
}
