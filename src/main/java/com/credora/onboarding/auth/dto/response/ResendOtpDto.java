package com.credora.onboarding.auth.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendOtpDto(
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email
) {
}
