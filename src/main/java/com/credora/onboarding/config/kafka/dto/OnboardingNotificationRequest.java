package com.credora.onboarding.config.kafka.dto;

public record OnboardingNotificationRequest(
        String customerFirstname,
        String customerLastname,
        String customerEmail,
        String phoneNumber
) {
}