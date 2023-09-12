package com.cs203g3.ticketing.security.auth;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailTakenException extends RuntimeException {
    public EmailTakenException(String email) {
        super("Email " + email + " is already taken.");
    }
}
