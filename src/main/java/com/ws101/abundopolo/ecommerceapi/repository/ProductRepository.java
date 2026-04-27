package com.ws101.abundopolo.ecommerceapi.repository;

import com.ws101.abundopolo.ecommerceapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find by category (case insensitive)
    List<Product> findByCategoryIgnoreCase(String category);
    
    // Find by name containing keyword
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find by price range
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    
    // Find low stock products
    List<Product> findByStockQuantityLessThan(Integer threshold);
    
    // Custom JPQL query
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    List<Product> findProductsInPriceRange(@Param("minPrice") Double minPrice, 
                                           @Param("maxPrice") Double maxPrice);
    
    @Query(value = "SELECT * FROM products WHERE stock_quantity < :threshold", 
           nativeQuery = true)
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
}