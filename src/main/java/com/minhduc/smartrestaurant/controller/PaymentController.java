package com.minhduc.smartrestaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.request.ReqCheckoutPaymentDTO;
import com.minhduc.smartrestaurant.domain.response.ResPaymentDTO;
import com.minhduc.smartrestaurant.service.PaymentService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/checkout")
    @ApiMessage("Checkout payment for order")
    public ResponseEntity<ResPaymentDTO> checkout(@Valid @RequestBody ReqCheckoutPaymentDTO reqDTO)
            throws IdInvalidException {
        ResPaymentDTO resDTO = this.paymentService.handleCheckout(reqDTO);
        return ResponseEntity.ok(resDTO);
    }
}