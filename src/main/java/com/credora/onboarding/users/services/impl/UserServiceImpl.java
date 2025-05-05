package com.credora.onboarding.users.services.impl;

import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.common.dto.StringResponseDto;
import com.credora.onboarding.users.entities.User;
import com.credora.onboarding.users.repositories.UserRepository;
import com.credora.onboarding.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public User createUser(RegisterDto registerDto) {
    User user = User
            .builder()
            .firstName(registerDto.firstName())
            .lastName(registerDto.lastName())
            .email(registerDto.email())
            .password(registerDto.password())
            .phoneNumber(registerDto.phoneNumber())
            .build();

    return userRepository.save(user);
  }

  @Override
  public StringResponseDto getUserDetails(UUID userId) {
    System.out.println("The name is Shazzar, and he is getting married to Favour");
    return new StringResponseDto("The name is Shazzar, and he is getting married to Favour");
  }
}
