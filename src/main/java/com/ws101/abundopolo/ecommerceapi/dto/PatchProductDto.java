package com.ws101.abundopolo.ecommerceapi.dto;

import com.ws101.abundopolo.ecommerceapi.model.Product;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PatchProductDto(
        @Size(min = 1, max = 100, message = "Product name must be between 1 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        @Positive(message = "Price must be positive")
        Double price,

        @Size(max = 100, message = "Category must be at most 100 characters")
        String category,

        @Positive(message = "Stock quantity must be positive")
        Integer stockQuantity,

        @Size(max = 500, message = "Image URL must be at most 500 characters")
        String imageUrl
) {
    public Product toProduct() {
        Product product = new Product();
        product.setName(name == null ? null : name.trim());
        product.setDescription(description);
        product.setPrice(price == null ? 0 : price);
        product.setCategory(category);
        product.setStockQuantity(stockQuantity == null ? -1 : stockQuantity);
        product.setImageUrl(imageUrl);
        return product;
    }
}
