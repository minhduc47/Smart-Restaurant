package com.minhduc.smartrestaurant.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhduc.smartrestaurant.domain.Order;
import com.minhduc.smartrestaurant.domain.request.ReqCreateOrderDTO;
import com.minhduc.smartrestaurant.domain.response.ResOrderDTO;
import com.minhduc.smartrestaurant.domain.response.ResultPaginationDTO;
import com.minhduc.smartrestaurant.service.OrderService;
import com.minhduc.smartrestaurant.util.annotation.ApiMessage;
import com.minhduc.smartrestaurant.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/orders/{id}")
    @ApiMessage("Get order by id")
    public ResponseEntity<ResOrderDTO> fetchOrderByID(@PathVariable Long id) throws IdInvalidException {
        Order order = orderService.handleFetchOrderById(id);
        ResOrderDTO resDTO = orderService.convertToResOrderDTO(order);
        return ResponseEntity.ok(resDTO);
    }

    @GetMapping("/orders")
    @ApiMessage("Fetch all orders")
    public ResponseEntity<ResultPaginationDTO> fetchAllOrders(
            @Parameter(name = "filter", description = "Query filter (VD: name ~ 'duck')") @Filter Specification<Order> spec,
            @ParameterObject Pageable pageable) {
        ResultPaginationDTO result = orderService.fetchAllOrders(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/orders/{id}")
    @ApiMessage("Update order by id")
    public ResponseEntity<ResOrderDTO> updateOrder(@PathVariable Long id, @Valid @RequestBody ReqCreateOrderDTO reqDTO)
            throws IdInvalidException {
        Order updatedOrder = orderService.handleUpdateOrder(id, reqDTO);
        ResOrderDTO resDTO = orderService.convertToResOrderDTO(updatedOrder);
        return ResponseEntity.ok(resDTO);
    }
}
