package com.cs203g3.ticketing.email;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code=HttpStatus.INTERNAL_SERVER_ERROR, reason="Email sending failed")
public class EmailException extends RuntimeException {
    public EmailException(String message) {
        super(message);
    }
}
