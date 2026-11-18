package com.aatzhmair.common.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(
    UUID orderId, UUID userId, List<OrderLineItem> lineItems, BigDecimal totalAmount,
    ShippingAddress shippingAddress, Instant placedAt
) {
    public record OrderLineItem(UUID productId, int quantity, BigDecimal unitPrice) {}

    public record ShippingAddress(String recipientName, String line1, String line2,
                                   String city, String state, String postalCode, String country) {}
}
