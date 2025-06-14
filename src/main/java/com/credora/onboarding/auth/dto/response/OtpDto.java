package com.credora.onboarding.auth.dto.response;

import jakarta.validation.constraints.*;

public record OtpDto(
        @Min(value = 100000, message = "OTP must be at least 6 digits")
        @Max(value = 999999, message = "OTP must be at most 6 digits")
        int otp,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+?\\d{10,15}$", message = "Phone number must be valid")
        String phoneNumber
) {
}
