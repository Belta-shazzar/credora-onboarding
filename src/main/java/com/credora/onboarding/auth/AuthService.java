package com.credora.onboarding.auth;

import com.credora.onboarding.auth.dto.OtpDto;
import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.auth.dto.ResendOtpDto;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.config.kafka.KafkaProducerService;
import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import com.credora.onboarding.config.redis.RedisService;
import com.credora.onboarding.exception.custom.BadRequestException;
import com.credora.onboarding.exception.custom.ConflictException;
import com.credora.onboarding.exception.custom.NotFoundException;
import com.credora.onboarding.exception.custom.UnauthorizedException;
import com.credora.onboarding.users.entities.User;
import com.credora.onboarding.users.enums.UserStatus;
import com.credora.onboarding.users.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserService userService;
  private final KafkaProducerService producerService;
  private final PasswordEncoder passwordEncoder;
  private final RedisService redisService;
  private final ObjectMapper objectMapper;

  public StringResponseDto registerUser(RegisterDto request) throws ConflictException {
    Optional<User> existingUser = userService.getUserByEmail(request.getEmail());

    if (existingUser.isPresent()) {
      throw new ConflictException("Email is already registered");
    }
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    User user = this.userService.createUser(request);

    producerService.sendOnboardingConfirmationNotification(new OnboardingNotificationRequest(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber()
    ));

    return new StringResponseDto("User registered successfully");
  }

  public StringResponseDto verifyOtp(OtpDto otpDto) throws UnauthorizedException, JsonProcessingException, NotFoundException {
    String redisKey = String.format("onboarding-otp:%s", otpDto.phoneNumber());
    String cachedOtp = redisService.getMessage(redisKey);

    if (cachedOtp == null) {
      throw new UnauthorizedException("Invalid or expired token");
    }

    Map<String, String> cachedOtpMap = objectMapper.readValue(cachedOtp, new TypeReference<>() {
    });

    if (!cachedOtpMap.get("otp").equals(String.valueOf(otpDto.otp()))
            || cachedOtpMap.get("email").isEmpty()) {
      throw new UnauthorizedException("Incorrect otp");
    }

    User user = userService.getUserByEmail(cachedOtpMap.get("email"))
            .orElseThrow(() -> new NotFoundException("User not found"));
    user.setAccountStatus(UserStatus.ACTIVE);
    this.userService.updateUser(user);

    return new StringResponseDto("Account is activated successfully");
  }

  public StringResponseDto resendOtp(ResendOtpDto resendOtpDto) throws NotFoundException, BadRequestException {
    User user = userService.getUserByEmail(resendOtpDto.email())
            .orElseThrow(() -> new NotFoundException("User with email not found"));

    if (user.getAccountStatus() != UserStatus.PENDING) {
      throw new BadRequestException("Account is already active");
    }

    producerService.sendOnboardingConfirmationNotification(new OnboardingNotificationRequest(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber()
    ));

    return new StringResponseDto("OTP has been sent");
  }
}
