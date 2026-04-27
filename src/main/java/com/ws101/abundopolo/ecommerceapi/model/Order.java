package com.ws101.abundopolo.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity - Represents customer orders in the e-commerce system.
 * 
 * @author Abundo Polo
 * @version 1.0
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    /**
     * Primary key - Auto-generated unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Customer's full name
     */
    @Column(nullable = false)
    private String customerName;
    
    /**
     * Unique order number (e.g., ORD-20241215-001)
     */
    @Column(unique = true)
    private String orderNumber;
    
    /**
     * Date and time when order was placed
     */
    private LocalDateTime orderDate;
    
    /**
     * Total amount of the order
     */
    private Double totalAmount;
    
    /**
     * Order status: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    /**
     * One-to-Many relationship: One Order has many OrderItems
     * CascadeType.ALL - When order is saved/deleted, items are also saved/deleted
     * orphanRemoval = true - Removes items when removed from collection
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();
    
    /**
     * Enum for Order Status
     */
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED
    }
    
    /**
     * Automatically set order date and generate order number before persisting
     */
    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        orderNumber = "ORD-" + System.currentTimeMillis();
    }
}