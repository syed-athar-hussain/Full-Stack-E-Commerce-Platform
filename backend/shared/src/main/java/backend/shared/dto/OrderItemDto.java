package backend.shared.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemDto(
	    @NotNull UUID productId,
	    @Min(1) int quantity,
	    BigDecimal price
) {}