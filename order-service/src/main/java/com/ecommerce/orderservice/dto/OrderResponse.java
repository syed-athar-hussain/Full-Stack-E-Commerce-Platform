package com.aatzhmair.orderservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id, String status, BigDecimal totalAmount, String failureReason,
    List<LineItemResponse> lineItems, Instant createdAt
) {
    public record LineItemResponse(UUID productId, int quantity, BigDecimal unitPrice) {}
}