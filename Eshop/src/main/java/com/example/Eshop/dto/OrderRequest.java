package com.example.Eshop.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record OrderRequest(
    @Valid @NotNull Customer customer,
    @Valid @NotNull Address shippingAddress,
    @NotBlank String shippingMethod, // "COURIER" | "PICKUP"
    @NotBlank String paymentMethod,  // "CARD" | "COD" | "BANK"
    String note,
    @Size(min = 1) @Valid List<Item> items,
    @Valid @NotNull Amounts amounts
) {
  public record Customer(
      @NotBlank String firstName,
      @NotBlank String lastName,
      @Email @NotBlank String email,
      @NotBlank String phone
  ) {}
  public record Address(
      @NotBlank String country,
      @NotBlank String city,
      @NotBlank String zip,
      @NotBlank String street
  ) {}
  public record Item(
      @NotBlank String productId,
      @NotBlank String name,
      @Positive double unitPrice,
      @Positive int quantity
  ) {}
  public record Amounts(
      @NotBlank String currency, // "EUR"
      @PositiveOrZero double subtotal,
      @PositiveOrZero double shipping,
      @PositiveOrZero double total
  ) {}
}
