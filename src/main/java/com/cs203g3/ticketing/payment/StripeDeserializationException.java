package com.cs203g3.ticketing.payment;

public class StripeDeserializationException extends RuntimeException {
    public StripeDeserializationException() {
        super("Stripe unable to deserialize input, likely API mismatch.");
    }
}
