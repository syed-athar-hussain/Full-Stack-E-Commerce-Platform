package backend.shared.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record PaymentDto(
	    @NotBlank String token,
	    @Min(0) BigDecimal amount
) {}