package com.ws101.abundopolo.ecommerceapi.dto;

import com.ws101.abundopolo.ecommerceapi.model.Order;

import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        String customerName,
        String orderNumber,
        LocalDateTime orderDate,
        Double totalAmount,
        Order.OrderStatus status
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getOrderNumber(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus()
        );
    }
}
