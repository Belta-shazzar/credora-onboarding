package com.credora.onboarding.auth.dto.request;

public record LoginResponseDto(
        String firstName,
        String lastName,
        String email,
        String accessToken,
        String refreshToken
) {
}
