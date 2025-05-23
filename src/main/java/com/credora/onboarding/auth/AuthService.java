package com.credora.onboarding.auth;

import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.config.kafka.KafkaProducerService;
import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import com.credora.onboarding.exception.custom.ConflictException;
import com.credora.onboarding.users.entities.User;
import com.credora.onboarding.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;
  private final KafkaProducerService producerService;
  private final PasswordEncoder passwordEncoder;

  public StringResponseDto registerUser(RegisterDto request) throws ConflictException {
    Optional<User> existingUser = userService.getUserByEmail(request.getEmail());

    if (existingUser.isPresent()) {
      throw new ConflictException("Email is already registered");
    }
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    User user = this.userService.createUser(request);

    //  TODO: Publish an event for the notification service
    producerService.sendOnboardingConfirmationNotification(new OnboardingNotificationRequest(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            ""
    ));
    System.out.println("Code end");

    return new StringResponseDto("User registered successfully");
  }
}
