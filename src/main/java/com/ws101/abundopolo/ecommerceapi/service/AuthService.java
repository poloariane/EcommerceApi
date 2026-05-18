package com.ws101.abundopolo.ecommerceapi.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ws101.abundopolo.ecommerceapi.dto.AuthResponse;
import com.ws101.abundopolo.ecommerceapi.dto.LoginRequest;
import com.ws101.abundopolo.ecommerceapi.dto.RegisterRequest;
import com.ws101.abundopolo.ecommerceapi.dto.UserResponse;
import com.ws101.abundopolo.ecommerceapi.model.Role;
import com.ws101.abundopolo.ecommerceapi.model.User;
import com.ws101.abundopolo.ecommerceapi.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        Role requestedRole = parseRole(request.getRole());

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username is already taken");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(requestedRole);

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getRole());
    }

    /**
     * Authenticates user with username and password, returns JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        try {
            String username = request.getUsername().trim();
            String password = request.getPassword();

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // Get the authenticated user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generate JWT token
            String token = jwtUtil.generateToken(userDetails);
            
            // Get user from database to retrieve role
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            return new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getRole().name(),
                    "Login successful"
            );
        } catch (AuthenticationException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid username or password");
        }
    }

    private Role parseRole(String role) {
        if (role == null || role.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        String normalizedRole = role.trim().toUpperCase();

        if ("SHOPPER".equals(normalizedRole)) {
            return Role.USER;
        }

        try {
            return Role.valueOf(normalizedRole);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Role must be one of: USER, SHOPPER, SELLER, ADMIN");
        }
    }
}
