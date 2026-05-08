package com.ws101.abundopolo.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateOrderDto(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotNull(message = "Total amount is required")
        @Positive(message = "Total amount must be positive")
        Double totalAmount
) {
}
