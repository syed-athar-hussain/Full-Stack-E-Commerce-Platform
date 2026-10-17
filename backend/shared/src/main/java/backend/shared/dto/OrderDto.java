package backend.shared.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderDto(
    UUID id,
    @NotNull UUID userId,
    @NotNull List<OrderItemDto> items,
    BigDecimal totalAmount,
    String status
) {}