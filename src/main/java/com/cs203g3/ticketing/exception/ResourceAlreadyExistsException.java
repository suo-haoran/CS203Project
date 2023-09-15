package com.cs203g3.ticketing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONFLICT, reason="Resource already exists")
public class ResourceAlreadyExistsException extends RuntimeException {
    public <T> ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
