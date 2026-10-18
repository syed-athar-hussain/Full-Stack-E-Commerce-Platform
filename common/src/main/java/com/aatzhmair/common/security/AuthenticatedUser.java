package com.aatzhmair.common.security;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String username, String role) {}
