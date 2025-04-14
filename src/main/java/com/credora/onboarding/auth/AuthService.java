package com.credora.onboarding.auth;

import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.exception.custom.ConflictException;
import com.credora.onboarding.users.entities.User;
import com.credora.onboarding.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;

  public StringResponseDto registerUser(RegisterDto request) throws ConflictException {
    Optional<User> existingUser = userService.getUserByEmail(request.email());

      if (existingUser.isPresent()) {
         throw new ConflictException("Email is already registered");
      }

      this.userService.createUser(request);

//      TODO: Publish an event for the notification service

    return new StringResponseDto("User registered successfully");
  }
}
