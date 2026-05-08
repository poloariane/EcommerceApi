package com.ws101.abundopolo.ecommerceapi.dto;

import com.ws101.abundopolo.ecommerceapi.model.Role;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        Set<Role> roles
) {
}
