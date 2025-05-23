package com.credora.onboarding.auth;

import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.exception.custom.ConflictException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @Value("${application.buzz}")
  private String buzzword;

  @PostMapping("register")
  @ResponseBody
  public ResponseEntity<StringResponseDto> register(@Valid @RequestBody RegisterDto register
  ) throws ConflictException {
    StringResponseDto responseDto = this.authService.registerUser(register);
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(responseDto);
  }

  @GetMapping("vault")
  @ResponseBody
  public ResponseEntity<StringResponseDto> vaultTest() {

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new StringResponseDto(buzzword));
  }
}

