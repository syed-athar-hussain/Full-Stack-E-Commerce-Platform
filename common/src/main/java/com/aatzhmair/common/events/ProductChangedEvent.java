package com.aatzhmair.common.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductChangedEvent(
        UUID productId, String name, String description, String category, BigDecimal price, Instant changedAt
) {}
