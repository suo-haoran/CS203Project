package com.cs203g3.ticketing.email.htmlToPdf;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR, reason = "PDF generation failed")
public class PdfException extends RuntimeException {
    public PdfException(String message) {
        super(message);
    }
}
