package com.ws101.abundopolo.ecommerceapi.repository;

import com.ws101.abundopolo.ecommerceapi.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity operations.
 * 
 * @author Abundo Polo
 * @version 1.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Finds orders by customer name
     */
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
    
    /**
     * Finds orders by status
     */
    List<Order> findByStatus(Order.OrderStatus status);
    
    /**
     * Finds orders placed after a specific date
     */
    List<Order> findByOrderDateAfter(LocalDateTime date);
    
    /**
     * Custom JPQL query to find recent orders with their items
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.orderDate >= :since")
    List<Order> findRecentOrdersWithItems(@Param("since") LocalDateTime since);
}