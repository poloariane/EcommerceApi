package com.ws101.abundopolo.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OrderItem Entity - Represents individual items within an order.
 * 
 * @author Abundo Polo
 * @version 1.0
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    /**
     * Primary key - Auto-generated unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Quantity of the product ordered
     */
    private Integer quantity;
    
    /**
     * Price of the product at the time of order (preserved for history)
     */
    private Double unitPrice;
    
    /**
     * Many-to-One relationship: Many OrderItems belong to one Order
     * FetchType.LAZY - Load order only when accessed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
    /**
     * Many-to-One relationship: Many OrderItems reference one Product
     * FetchType.LAZY - Load product only when accessed
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
}