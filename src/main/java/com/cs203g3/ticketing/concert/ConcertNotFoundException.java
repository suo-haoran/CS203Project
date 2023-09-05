package com.cs203g3.ticketing.concert;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConcertNotFoundException extends RuntimeException {
    public ConcertNotFoundException(Long id) {
        super("Could not find Concert with ID #" + id);
    }
}
