package com.cs203g3.ticketing.exception;


public class ResourceAlreadyExistsException extends RuntimeException {
    public <T> ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
