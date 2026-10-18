package com.aatzhmair.userservice.service;

import com.aatzhmair.userservice.dto.AuthResponse;
import com.aatzhmair.userservice.dto.LoginRequest;
import com.aatzhmair.userservice.dto.RegisterRequest;
import com.aatzhmair.userservice.entity.Role;
import com.aatzhmair.userservice.entity.User;
import com.aatzhmair.userservice.exception.DuplicateUserException;
import com.aatzhmair.userservice.repository.UserRepository;
import com.aatzhmair.userservice.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsernameOrEmail(request.username(), request.email())) {
            throw new DuplicateUserException("Username or email already in use");
        }

        User user = new User(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                Role.CUSTOMER
        );
        userRepository.save(user);
        return issueToken(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return issueToken(user);
    }

    private AuthResponse issueToken(User user) {
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        long expiresAt = System.currentTimeMillis() + jwtService.expirationMs();
        return new AuthResponse(token, user.getUsername(), user.getRole().name(), expiresAt);
    }
}