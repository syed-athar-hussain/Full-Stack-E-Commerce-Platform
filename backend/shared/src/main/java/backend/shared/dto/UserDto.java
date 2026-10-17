package backend.shared.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record UserDto(
    UUID id,
    @NotBlank @Email String email,
    @Size(min = 8) String password,
    String fullName,
    String role
) {}
