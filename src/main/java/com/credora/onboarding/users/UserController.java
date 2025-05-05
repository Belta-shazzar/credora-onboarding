package com.credora.onboarding.users;

import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{userId}")
  @ResponseBody
  public ResponseEntity<StringResponseDto> getUserDetails(@PathVariable("userId") UUID userId) {
    StringResponseDto responseDto = this.userService.getUserDetails(userId);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(responseDto);
  }

}
