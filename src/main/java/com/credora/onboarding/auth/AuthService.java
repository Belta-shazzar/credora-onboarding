package com.credora.onboarding.auth;

import com.credora.onboarding.auth.dto.request.LoginResponseDto;
import com.credora.onboarding.auth.dto.response.LoginDto;
import com.credora.onboarding.auth.dto.response.OtpDto;
import com.credora.onboarding.auth.dto.response.RegisterDto;
import com.credora.onboarding.auth.dto.response.ResendOtpDto;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.config.kafka.KafkaProducerService;
import com.credora.onboarding.config.kafka.dto.OnboardingNotificationRequest;
import com.credora.onboarding.config.redis.RedisService;
import com.credora.onboarding.config.security.jwt.JwtUtil;
import com.credora.onboarding.config.security.user.AppUser;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;

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

  public LoginResponseDto login(LoginDto loginDto) throws Exception {
    try {
    Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
            loginDto.email(),
            loginDto.password()
    ));

    AppUser appUser = (AppUser) authentication.getPrincipal();

    String accessToken = this.jwtUtil.generateAccessToken(appUser);
    String refreshToken = this.jwtUtil.generateRefreshToken(appUser);

    User user = appUser.getUser();

    return new LoginResponseDto(
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            accessToken,
            refreshToken
    );
    } catch (Exception e) {
      throw new BadRequestException("Incorrect email or password");
    }
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

  public void getAuthenticatedUser() {

  }

  public void getRefreshToken(HttpServletRequest request, HttpServletResponse response) {
//    String refreshToken;
//    String email;
//    String authPrefix = "Bearer ";
//    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//    if (authHeader != null || !authHeader.startsWith(authPrefix)) {
////      TODO: Figure if to return or throw an error.
//    }
//
//    refreshToken = authHeader.substring(authPrefix.length());
//    email = jwtUtil.getUserNameFromToken(refreshToken);
//
//    System.out.println("The auth header: " + email);
  }
}
