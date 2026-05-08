package com.ws101.abundopolo.ecommerceapi.dto;

import com.ws101.abundopolo.ecommerceapi.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateProductDto(
        @NotBlank(message = "Product name is required")
        @Size(max = 100, message = "Product name must be at most 100 characters")
        String name,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,

        @Size(max = 100, message = "Category must be at most 100 characters")
        String category,

        @NotNull(message = "Stock quantity is required")
        @Positive(message = "Stock quantity must be positive")
        Integer stockQuantity,

        @Size(max = 500, message = "Image URL must be at most 500 characters")
        String imageUrl
) {
    public Product toProduct() {
        Product product = new Product();
        product.setName(name.trim());
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setStockQuantity(stockQuantity);
        product.setImageUrl(imageUrl);
        return product;
    }
}
