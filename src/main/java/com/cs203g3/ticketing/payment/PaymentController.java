package com.cs203g3.ticketing.payment;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;


@RestController
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService ps) {
        this.paymentService = ps;
    }

    @PostMapping("/v1/payment-completed")
    public void processPaymentCompleted(HttpServletRequest req, @RequestBody String stripePayload) throws IOException {
        paymentService.processPaymentCompleted(req.getHeader("Stripe-Signature"), stripePayload);
    }
}
