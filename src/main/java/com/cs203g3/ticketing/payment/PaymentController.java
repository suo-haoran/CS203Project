package com.cs203g3.ticketing.payment;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cs203g3.ticketing.payment.dto.PaymentRequestDto;

import jakarta.validation.Valid;

@RestController
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService ps) {
        this.paymentService = ps;
    }

    @PostMapping("/payment-completed")
    public void processPaymentCompleted(@Valid @RequestBody PaymentRequestDto paymentDto) {
        paymentService.processPaymentCompleted(paymentDto);
    }
}
