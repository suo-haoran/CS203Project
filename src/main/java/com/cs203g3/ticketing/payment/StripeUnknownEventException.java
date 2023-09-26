package com.cs203g3.ticketing.payment;

public class StripeUnknownEventException extends RuntimeException{
    public StripeUnknownEventException() {
        super("The original developer has no idea wtf Stripe just sent over, please check the event type Stripe is sending.");
    }
}
