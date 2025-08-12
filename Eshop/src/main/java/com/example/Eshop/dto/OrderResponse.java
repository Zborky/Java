package com.example.Eshop.dto;

import java.time.OffsetDateTime;

public record OrderResponse(
    Long id,
    String orderNumber,
    String currency,
    double subtotal,
    double shipping,
    double total,
    OffsetDateTime createdAt
) {}
