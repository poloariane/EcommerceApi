package com.ws101.abundopolo.ecommerceapi.service;

import com.ws101.abundopolo.ecommerceapi.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Service layer for product-related business logic.
 * <p>
 * This class manages an in-memory collection of {@link Product} objects.
 * It provides CRUD operations, filtering, and ID generation.
 * The service is a singleton managed by Spring's dependency injection container.
 * </p>
 *
 * @author Your Name & Partner Name
 * @version 1.0
 * @see Product
 * @see com.ws101.abundo.polo.ecommerceapi.controller.ProductController
 */
@Service
public class ProductService {

    /** In-memory storage for all products. */
    private final List<Product> productList = new ArrayList<>();

    /** Thread-safe ID generator to ensure unique product IDs. */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * Constructor that initializes the product list with at least 10 sample products.
     * These samples allow immediate testing without needing to create products first.
     */
    public ProductService() {
        // Initialize with 10 sample products (at least 10 required by lab)
        productList.add(new Product(idGenerator.getAndIncrement(), "Laptop", "High-performance laptop", 1200.00, "Electronics", 10, "laptop.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Mouse", "Wireless mouse", 25.99, "Electronics", 50, "mouse.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Notebook", "A4 ruled notebook", 2.49, "Stationery", 200, "notebook.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Smartphone", "5G Android phone", 699.99, "Electronics", 30, "phone.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Desk Lamp", "LED desk lamp with dimmer", 45.00, "Furniture", 15, "lamp.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Backpack", "Water-resistant laptop backpack", 59.99, "Accessories", 40, "bag.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Monitor", "27-inch 4K monitor", 350.00, "Electronics", 12, "monitor.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Coffee Mug", "Ceramic coffee mug", 12.99, "Kitchen", 100, "mug.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "Headphones", "Noise-cancelling headphones", 89.99, "Electronics", 25, "headphones.jpg"));
        productList.add(new Product(idGenerator.getAndIncrement(), "T-Shirt", "Cotton graphic t-shirt", 19.99, "Clothing", 75, "shirt.jpg"));
    }

    /**
     * Retrieves all products from the in-memory list.
     *
     * @return a new {@code ArrayList} containing all products (never {@code null}).
     *         The returned list is a copy, so modifications do not affect the internal storage.
     */
    public List<Product> getAllProducts() {
        // Return a copy to prevent external modification of the internal list
        return new ArrayList<>(productList);
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param id the product ID to search for (must not be {@code null})
     * @return an {@code Optional} containing the product if found, otherwise empty
     */
    public Optional<Product> getProductById(Long id) {
        // Stream through the list, filter by ID, and return the first match (if any)
        return productList.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Creates a new product and adds it to the in-memory storage.
     * <p>
     * The ID is automatically generated using {@link AtomicLong}.
     * The provided product object should not have an ID set (it will be overwritten).
     * </p>
     *
     * @param product the product to create (must not be {@code null})
     * @return the created product with its newly assigned ID
     */
    public Product createProduct(Product product) {
        // Assign a new unique ID before adding to the list
        product.setId(idGenerator.getAndIncrement());
        productList.add(product);
        return product;
    }

    /**
     * Replaces an existing product entirely with new data (full update).
     * <p>
     * This method implements the PUT semantics: the existing product is completely
     * overwritten with the fields from {@code updatedProduct}.
     * </p>
     *
     * @param id the ID of the product to update
     * @param updatedProduct the product data that will replace the existing one
     * @return an {@code Optional} containing the updated product if found, otherwise empty
     */
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        // Use getProductById to find the existing product, then update all fields
        return getProductById(id).map(existing -> {
            existing.setName(updatedProduct.getName());
            existing.setDescription(updatedProduct.getDescription());
            existing.setPrice(updatedProduct.getPrice());
            existing.setCategory(updatedProduct.getCategory());
            existing.setStockQuantity(updatedProduct.getStockQuantity());
            existing.setImageUrl(updatedProduct.getImageUrl());
            return existing;
        });
    }

    /**
     * Partially updates an existing product (PATCH semantics).
     * <p>
     * Only fields that are non-{@code null} in {@code partialProduct} will be updated.
     * Fields with {@code null} values are ignored (left unchanged).
     * For {@code price}, only positive values trigger an update.
     * For {@code stockQuantity}, only non-negative values trigger an update.
     * </p>
     *
     * @param id the ID of the product to patch
     * @param partialProduct the product containing only the fields to update
     * @return an {@code Optional} containing the updated product if found, otherwise empty
     */
    public Optional<Product> patchProduct(Long id, Product partialProduct) {
        return getProductById(id).map(existing -> {
            // Update only the fields that were provided (non-null)
            if (partialProduct.getName() != null) {
                existing.setName(partialProduct.getName());
            }
            if (partialProduct.getDescription() != null) {
                existing.setDescription(partialProduct.getDescription());
            }
            // Only update price if it's positive (0 is not a valid price)
            if (partialProduct.getPrice() > 0) {
                existing.setPrice(partialProduct.getPrice());
            }
            if (partialProduct.getCategory() != null) {
                existing.setCategory(partialProduct.getCategory());
            }
            // Only update stock if it's non-negative
            if (partialProduct.getStockQuantity() >= 0) {
                existing.setStockQuantity(partialProduct.getStockQuantity());
            }
            if (partialProduct.getImageUrl() != null) {
                existing.setImageUrl(partialProduct.getImageUrl());
            }
            return existing;
        });
    }

    /**
     * Deletes a product by its ID.
     *
     * @param id the ID of the product to delete
     * @return {@code true} if a product was removed, {@code false} if no product with the given ID existed
     */
    public boolean deleteProduct(Long id) {
        // removeIf returns true if any element was removed
        return productList.removeIf(p -> p.getId().equals(id));
    }

    /**
     * Filters products based on a specified criterion.
     * <p>
     * Supported filter types:
     * <ul>
     *   <li><b>category</b> – case‑insensitive exact match (e.g., "Electronics")</li>
     *   <li><b>price</b>   – returns products with price ≤ the given value</li>
     *   <li><b>name</b>    – case‑insensitive substring match (e.g., "book" matches "Notebook")</li>
     * </ul>
     * </p>
     *
     * @param filterType  the field to filter by ("category", "price", or "name")
     * @param filterValue the value to compare against (for "price", a parsable double)
     * @return a list of products that satisfy the filter (never {@code null})
     * @throws IllegalArgumentException if {@code filterType} is not recognized or
     *                                  if {@code filterValue} cannot be parsed for "price"
     */
    public List<Product> filterProducts(String filterType, String filterValue) {
        // Switch on the filter type (case‑insensitive)
        switch (filterType.toLowerCase()) {
            case "category":
                // Exact match, ignoring case
                return productList.stream()
                        .filter(p -> p.getCategory().equalsIgnoreCase(filterValue))
                        .collect(Collectors.toList());

            case "price":
                // Parse the filterValue as a double (may throw NumberFormatException)
                double price = Double.parseDouble(filterValue);
                // Return all products with price less than or equal to the given value
                return productList.stream()
                        .filter(p -> p.getPrice() <= price)
                        .collect(Collectors.toList());

            case "name":
                // Substring match, ignoring case
                return productList.stream()
                        .filter(p -> p.getName().toLowerCase().contains(filterValue.toLowerCase()))
                        .collect(Collectors.toList());

            default:
                // Unknown filter type – throw an exception that will be caught by the global handler
                throw new IllegalArgumentException("Invalid filterType: " + filterType);
        }
    }
}