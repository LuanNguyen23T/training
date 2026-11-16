package com.sun.training.employeemanagement.controller;

import com.sun.training.employeemanagement.dto.AuthResponseDto;
import com.sun.training.employeemanagement.dto.LoginRequestDto;
import com.sun.training.employeemanagement.dto.RegisterRequestDto;
import com.sun.training.employeemanagement.service.AuthService;
import jakarta.validation.Valid;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing user registration and authentication endpoints (Module 9).
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    /**
     * Constructor injection for AuthService.
     *
     * @param authService auth service bean
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user.
     *
     * @param request validated registration payload
     * @return 201 Created with JWT auth response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        LOGGER.info("POST /api/v1/auth/register requested for username: {}", request.getUsername());
        AuthResponseDto response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param request validated login credentials
     * @return 200 OK with JWT auth response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LOGGER.info("POST /api/v1/auth/login requested for username: {}", request.getUsername());
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves current authenticated user profile information.
     *
     * @param authentication security authentication object
     * @return authenticated user details
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authentication.getName();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        return ResponseEntity.ok(Map.of(
                "username", username,
                "authorities", roles
        ));
    }
}

