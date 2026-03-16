package com.minhduc.smartrestaurant.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.request.ReqCheckoutPaymentDTO;
import com.minhduc.smartrestaurant.domain.request.ReqCreateVNPayDTO;
import com.minhduc.smartrestaurant.domain.response.ResPaymentDTO;
import com.minhduc.smartrestaurant.service.PaymentService;
import com.minhduc.smartrestaurant.service.VNPayService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final VNPayService vnPayService;

    public PaymentController(PaymentService paymentService, VNPayService vnPayService) {
        this.paymentService = paymentService;
        this.vnPayService = vnPayService;
    }

    @PostMapping("/checkout")
    @ApiMessage("Checkout payment for order")
    public ResponseEntity<ResPaymentDTO> checkout(@Valid @RequestBody ReqCheckoutPaymentDTO reqDTO)
            throws IdInvalidException {
        ResPaymentDTO resDTO = this.paymentService.handleCheckout(reqDTO);
        return ResponseEntity.ok(resDTO);
    }

    @PostMapping("/vnpay/create")
    @ApiMessage("Create VNPay payment url")
    public ResponseEntity<Map<String, String>> createVNPayPayment(@Valid @RequestBody ReqCreateVNPayDTO reqDTO,
            HttpServletRequest request)
            throws IdInvalidException, UnsupportedEncodingException {
        String ipAddress = this.vnPayService.getIpAddress(request);
        Map<String, String> response = this.paymentService.createVNPayPayment(reqDTO, ipAddress);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> handleVNPayIpn(@RequestParam Map<String, String> params)
            throws UnsupportedEncodingException {
        return ResponseEntity.ok(this.paymentService.handleVNPayIpn(params));
    }

    @GetMapping("/vnpay/return")
    @ApiMessage("Handle VNPay return")
    public ResponseEntity<Map<String, Object>> handleVNPayReturn(@RequestParam Map<String, String> params)
            throws UnsupportedEncodingException {
        return ResponseEntity.ok(this.paymentService.handleVNPayReturn(params));
    }
}