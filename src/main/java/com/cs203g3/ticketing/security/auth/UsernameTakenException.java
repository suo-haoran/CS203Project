package com.cs203g3.ticketing.security.auth;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UsernameTakenException extends RuntimeException {
    public UsernameTakenException(String name) {
        super("Username " + name + " is already taken.");
    }
}
