package com.cs203g3.ticketing.payment;

public class StripeSignatureVerificationException extends RuntimeException {
    public StripeSignatureVerificationException(String message) {
        super(message);
    }
}
