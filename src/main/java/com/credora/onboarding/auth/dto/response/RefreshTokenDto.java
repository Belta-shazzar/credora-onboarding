package com.credora.onboarding.auth.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshTokenDto(
        @NotBlank(message = "Email is required")
        @Size(min = 20, max = 500)
        String refreshToken
) {
}
