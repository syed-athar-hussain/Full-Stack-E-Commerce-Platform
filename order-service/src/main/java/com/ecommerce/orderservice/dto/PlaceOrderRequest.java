package com.aatzhmair.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PlaceOrderRequest(@NotEmpty @Valid List<LineItem> lineItems) {
    public record LineItem(@NotNull UUID productId, @Min(1) int quantity) {}
}