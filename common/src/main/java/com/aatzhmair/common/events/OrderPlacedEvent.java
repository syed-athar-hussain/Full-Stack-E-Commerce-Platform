package com.aatzhmair.common.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(
        UUID orderId, UUID userId, List<OrderLineItem> lineItems, BigDecimal totalAmount, Instant placedAt
) {
    public record OrderLineItem(UUID productId, int quantity, BigDecimal unitPrice) {}
}
