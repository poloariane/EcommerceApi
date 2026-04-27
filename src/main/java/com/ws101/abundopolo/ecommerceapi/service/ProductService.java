package com.ws101.abundopolo.ecommerceapi.service;

import com.ws101.abundopolo.ecommerceapi.model.Category;
import com.ws101.abundopolo.ecommerceapi.model.Product;
import com.ws101.abundopolo.ecommerceapi.repository.CategoryRepository;
import com.ws101.abundopolo.ecommerceapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
    
    @Transactional
    public Product createProduct(Product product, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
            product.setCategory(category.getName());
        }
        return productRepository.save(product);
    }
    
    @Transactional
    public Product updateProduct(Long id, Product productDetails, Long categoryId) {
        Product existingProduct = getProductById(id);
        
        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setStockQuantity(productDetails.getStockQuantity());
        existingProduct.setImageUrl(productDetails.getImageUrl());
        
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existingProduct.setCategory(category.getName());
        } else if (productDetails.getCategory() != null) {
            existingProduct.setCategory(productDetails.getCategory());
        }
        
        return productRepository.save(existingProduct);
    }
    
    @Transactional
    public Product patchProduct(Long id, Product partialProduct) {
        Product existingProduct = getProductById(id);
        
        if (partialProduct.getName() != null) {
            existingProduct.setName(partialProduct.getName());
        }
        if (partialProduct.getDescription() != null) {
            existingProduct.setDescription(partialProduct.getDescription());
        }
        if (partialProduct.getPrice() > 0) {
            existingProduct.setPrice(partialProduct.getPrice());
        }
        if (partialProduct.getCategory() != null) {
            existingProduct.setCategory(partialProduct.getCategory());
        }
        if (partialProduct.getStockQuantity() >= 0) {
            existingProduct.setStockQuantity(partialProduct.getStockQuantity());
        }
        if (partialProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(partialProduct.getImageUrl());
        }
        
        return productRepository.save(existingProduct);
    }
    
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    public List<Product> filterProducts(String filterType, String filterValue) {
        switch (filterType.toLowerCase()) {
            case "category":
                return productRepository.findByCategoryIgnoreCase(filterValue);
            case "price":
                double maxPrice = Double.parseDouble(filterValue);
                return productRepository.findByPriceBetween(0.0, maxPrice);
            case "name":
                return productRepository.findByNameContainingIgnoreCase(filterValue);
            default:
                throw new IllegalArgumentException("Invalid filter type: " + filterType);
        }
    }
}