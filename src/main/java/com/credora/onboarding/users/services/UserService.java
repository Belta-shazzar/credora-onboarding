package com.credora.onboarding.users.services;

import com.credora.onboarding.auth.dto.RegisterDto;
import com.credora.onboarding.users.entities.User;

import java.util.Optional;

public interface UserService {
  Optional<User> getUserByEmail(String email);
   User createUser(RegisterDto user);
}
