package com.ws101.abundopolo.ecommerceapi.controller;

import com.ws101.abundopolo.ecommerceapi.exception.GlobalExceptionHandler;
import com.ws101.abundopolo.ecommerceapi.service.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductControllerValidationTest {

    private LocalValidatorFactoryBean validator;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        ProductService productService = mock(ProductService.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProductController(productService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void tearDown() {
        validator.close();
    }

    @Test
    void createProductWithNegativePriceReturnsValidationErrors() throws Exception {
        String invalidProductJson = """
                {
                  "name": "Test product",
                  "description": "Invalid price request",
                  "price": -10.00,
                  "category": "Test",
                  "stockQuantity": 5,
                  "imageUrl": "/Images/products/test.jpg"
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType("application/json")
                        .content(invalidProductJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.errors", hasItem("Field 'price' Price must be positive")));
    }
}
