package com.cs203g3.ticketing.exception;

import java.util.UUID;


public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public <T> ResourceNotFoundException(Class<T> resource, Long id) {
        super(String.format("Could not find %s with ID #%d", resource.getSimpleName(), id));
    }

    public <T> ResourceNotFoundException(Class<T> resource, UUID uuid) {
        super(String.format("Could not find %s with UUID #%s", resource.getSimpleName(), uuid));
    }
}
