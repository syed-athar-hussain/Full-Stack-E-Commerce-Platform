package com.aatzhmair.productservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id, String name, String description, BigDecimal price,
        String category, int stockQuantity, String imageUrl
) {}
