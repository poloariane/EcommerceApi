package com.ws101.abundopolo.ecommerceapi.dto;

import com.ws101.abundopolo.ecommerceapi.model.Role;

public record UserResponse(
        Long id,
        String username,
        Role role
) {
}
