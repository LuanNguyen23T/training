package com.sun.training.employeemanagement.service;

import com.sun.training.employeemanagement.dto.AuthResponseDto;
import com.sun.training.employeemanagement.dto.LoginRequestDto;
import com.sun.training.employeemanagement.dto.RegisterRequestDto;
import com.sun.training.employeemanagement.entity.Role;
import com.sun.training.employeemanagement.entity.User;
import com.sun.training.employeemanagement.exception.DuplicateResourceException;
import com.sun.training.employeemanagement.repository.UserRepository;
import com.sun.training.employeemanagement.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing user registration and authentication workflows (Module 9).
 */
@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Constructor injection for auth service dependencies.
     *
     * @param userRepository user repository
     * @param passwordEncoder password encoder
     * @param authenticationManager authentication manager
     * @param tokenProvider JWT token utility
     */
    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Checks if a username already exists in the system.
     *
     * @param username username to check
     * @return true if exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return userRepository.existsByUsername(username.trim());
    }

    /**
     * Registers a new user account with encoded password and assigned role.
     *
     * @param request registration request containing username, password, and optional role
     * @return authentication response with generated JWT token
     */
    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {
        LOGGER.info("Attempting to register user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Tên đăng nhập '" + request.getUsername() + "' đã được sử dụng!");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.ROLE_USER;
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                role
        );

        User savedUser = userRepository.save(user);
        LOGGER.info("Successfully registered user: {} with role: {}", savedUser.getUsername(), savedUser.getRole());

        String token = tokenProvider.generateToken(savedUser.getUsername(), savedUser.getRole().name());
        return new AuthResponseDto(token, savedUser.getUsername(), savedUser.getRole().name());
    }

    /**
     * Authenticates user credentials and generates a JWT token on success.
     *
     * @param request login request credentials
     * @return authentication response with generated JWT token
     */
    public AuthResponseDto login(LoginRequestDto request) {
        LOGGER.info("Attempting authentication for user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUsername()));

        String token = tokenProvider.generateToken(user.getUsername(), user.getRole().name());
        LOGGER.info("User {} successfully authenticated", user.getUsername());

        return new AuthResponseDto(token, user.getUsername(), user.getRole().name());
    }
}
