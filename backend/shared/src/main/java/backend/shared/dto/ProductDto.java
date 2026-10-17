package backend.shared.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.UUID;

public record ProductDto(
    UUID id,
    @NotBlank String name,
    String description,
    @Min(0) BigDecimal price,
    @Min(0) int stock,
    String gender,
    String category,
    String subCategory,
    String productType,
    String colour,
    String usage,
    String productTitle,
    String image,
    String imageURL
) {}
