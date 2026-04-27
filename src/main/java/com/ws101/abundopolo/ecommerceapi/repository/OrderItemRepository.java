package com.ws101.abundopolo.ecommerceapi.repository;

import com.ws101.abundopolo.ecommerceapi.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for OrderItem entity operations.
 * 
 * @author Abundo Polo
 * @version 1.0
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * Finds all items belonging to a specific order
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * Finds all items for a specific product
     */
    List<OrderItem> findByProductId(Long productId);
}