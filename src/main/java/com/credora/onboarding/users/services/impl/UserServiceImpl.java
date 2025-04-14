package com.credora.onboarding.users.services.impl;

import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.users.entities.User;
import com.credora.onboarding.users.repositories.UserRepository;
import com.credora.onboarding.users.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
}
