package com.cs203g3.ticketing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(Class resource, Long id) {
        super(String.format("Could not find %s with ID #%d", resource.getSimpleName(), id));
    }
}
