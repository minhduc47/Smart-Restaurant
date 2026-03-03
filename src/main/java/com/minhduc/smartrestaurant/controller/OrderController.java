package com.minhduc.smartrestaurant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.Order;
import com.minhduc.smartrestaurant.domain.request.ReqCreateOrderDTO;
import com.minhduc.smartrestaurant.domain.response.ResOrderDTO;
import com.minhduc.smartrestaurant.service.OrderService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @ApiMessage("Create a new order")
    public ResponseEntity<ResOrderDTO> createOrder(@Valid @RequestBody ReqCreateOrderDTO reqDTO)
            throws IdInvalidException {
        Order newOrder = orderService.handleCreateOrder(reqDTO);
        ResOrderDTO resDTO = orderService.convertToResOrderDTO(newOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(resDTO);
    }
}
