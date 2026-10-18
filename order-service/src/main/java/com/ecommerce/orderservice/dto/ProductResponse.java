package com.aatzhmair.orderservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

// Order Service's own view of a product — deliberately not shared with Product Service's DTO.
// Each service owns its own contracts; common is for events, not for coupling REST shapes together.
public record ProductResponse(UUID id, String name, String description, BigDecimal price,
                               String category, int stockQuantity, String imageUrl) {}