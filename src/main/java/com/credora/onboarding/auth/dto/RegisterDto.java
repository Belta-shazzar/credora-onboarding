package com.credora.onboarding.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDto {
        @NotBlank(message = "First name is required")
        String firstName;

        @NotBlank(message = "Last name is required")
        String lastName;

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email;

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
        )
        String password;

        @NotBlank(message = "Phone number is required")
        String phoneNumber;
}
