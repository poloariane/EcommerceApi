package com.ws101.abundopolo.ecommerceapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Category Entity - Represents product categories in the e-commerce system.
 * 
 * @author Abundo Polo
 * @version 1.0
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    
    /**
     * Primary key - Auto-generated unique identifier
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Category name (e.g., "Electronics", "Clothing", "Cosmetics")
     * Cannot be null and must be unique
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    /**
     * Optional description of the category
     */
    private String description;
    
    /**
     * One-to-Many relationship: One Category has many Products
     * mappedBy = "category" refers to the 'category' field in Product entity
     * CascadeType.ALL - Operations cascade to child entities
     * FetchType.LAZY - Load products only when accessed
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();
    
    /**
     * Constructor for creating Category without products list
     */
    public Category(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}