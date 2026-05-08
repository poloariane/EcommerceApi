package com.ws101.abundopolo.ecommerceapi.service;

import com.ws101.abundopolo.ecommerceapi.dto.CreateOrderDto;
import com.ws101.abundopolo.ecommerceapi.dto.OrderResponse;
import com.ws101.abundopolo.ecommerceapi.model.Order;
import com.ws101.abundopolo.ecommerceapi.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderDto request) {
        Order order = new Order();
        order.setCustomerName(request.customerName().trim());
        order.setTotalAmount(request.totalAmount());
        order.setStatus(Order.OrderStatus.PROCESSING);

        return OrderResponse.from(orderRepository.save(order));
    }
}
