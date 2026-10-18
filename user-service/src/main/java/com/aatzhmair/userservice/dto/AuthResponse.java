package com.aatzhmair.userservice.dto;

public record AuthResponse(String token, String username, String role, long expiresAt) {}