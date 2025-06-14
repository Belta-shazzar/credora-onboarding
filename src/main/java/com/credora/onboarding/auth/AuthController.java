package com.credora.onboarding.auth;

import com.credora.onboarding.auth.dto.request.LoginResponseDto;
import com.credora.onboarding.auth.dto.response.*;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.config.security.user.AppUser;
import com.credora.onboarding.exception.custom.BadRequestException;
import com.credora.onboarding.exception.custom.ConflictException;
import com.credora.onboarding.exception.custom.NotFoundException;
import com.credora.onboarding.exception.custom.UnauthorizedException;
import com.credora.onboarding.users.dto.response.UserDto;
import com.credora.onboarding.users.entities.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("register")
  @ResponseBody
  public ResponseEntity<StringResponseDto> register(@Valid @RequestBody RegisterDto register
  ) throws ConflictException {
    StringResponseDto responseDto = this.authService.registerUser(register);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseDto);
  }

  @PostMapping("login")
  public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) throws Exception {
    LoginResponseDto responseDto = this.authService.login(loginDto);

    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }

  @PostMapping("verify-otp")
  public ResponseEntity<StringResponseDto> verifyOTP(@Valid @RequestBody OtpDto otpDto)
          throws UnauthorizedException, JsonProcessingException, NotFoundException {
    StringResponseDto responseDto = this.authService.verifyOtp(otpDto);

    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }

  @PostMapping("resend-otp")
  public ResponseEntity<StringResponseDto> resendOTP(@Valid @RequestBody ResendOtpDto resendOtpDto)
          throws NotFoundException, BadRequestException {
    StringResponseDto responseDto = this.authService.resendOtp(resendOtpDto);

    return ResponseEntity.status(HttpStatus.OK).body(responseDto);
  }

  @GetMapping("me")
  public ResponseEntity<UserDto> getAuthenticatedUser(@AuthenticationPrincipal AppUser appUser) {
    User user = appUser.getUser();

    return ResponseEntity.status(HttpStatus.OK).body(
            new UserDto(user.getFirstName(), user.getLastName(), user.getEmail())
    );
  }

  @PostMapping("refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
    this.authService.getRefreshToken(request, response);
  }
}

