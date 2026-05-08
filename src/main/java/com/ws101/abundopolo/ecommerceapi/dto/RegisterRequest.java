package com.ws101.abundopolo.ecommerceapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 8, max = 20, message = "Username must be between 8 and 20 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "(?i)USER|SHOPPER|SELLER|ADMIN", message = "Role must be one of: USER, SHOPPER, SELLER, ADMIN")
        String role
) {
}
