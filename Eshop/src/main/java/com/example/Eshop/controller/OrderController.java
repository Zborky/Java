package com.example.Eshop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Eshop.dto.OrderRequest;
import com.example.Eshop.dto.OrderResponse;
import com.example.Eshop.service.OrderService;

import jakarta.validation.Valid;

/**
 * REST controller for handling order-related operations.
 * 
 * <p>This controller provides an endpoint for creating new orders.
 * It accepts an {@link OrderRequest} object, validates it, 
 * and returns an {@link OrderResponse} with the created order details.</p>
 */
@RestController
@RequestMapping("/api/orders")
@Validated
@CrossOrigin(origins = {"*"})
public class OrderController {

    // Service layer dependency for handling business logic related to orders
    private final OrderService orderService;

    /**
     * Constructor for injecting the {@link OrderService}.
     * 
     * @param orderService the service responsible for order operations
     */
    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    /**
     * Creates a new order.
     * 
     * <p>This endpoint consumes a JSON body representing an {@link OrderRequest},
     * validates the input using Jakarta Bean Validation, and returns the details
     * of the created order in an {@link OrderResponse}.</p>
     * 
     * @param request the order request containing product and customer details
     * @return a ResponseEntity containing the created order's details
     */
    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request){
        // Call the service layer to create the order
        OrderResponse res = orderService.create(request);
        // Return HTTP 200 (OK) with the created order details
        return ResponseEntity.ok(res);
    }
}
