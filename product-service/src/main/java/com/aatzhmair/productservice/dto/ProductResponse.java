package com.aatzhmair.productservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
    UUID id, String name, String description, BigDecimal price, String category,
    String gender, String subCategory, String productType, String colour, String usage,
    int stockQuantity, String imageUrl
) {}
