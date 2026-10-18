package com.aatzhmair.productservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String description,
        @DecimalMin(value = "0.01") BigDecimal price,
        @NotBlank String category,
        @Min(0) int stockQuantity,
        String imageUrl
) {}
