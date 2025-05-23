package com.credora.onboarding.config.security.user;

import com.credora.onboarding.users.entities.User;
import com.credora.onboarding.users.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AppUserService implements UserDetailsService {
   private final UserRepository userRepository;

  public AppUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(username).orElseThrow(() ->
            new UsernameNotFoundException(String.format("user with %s %s not found", "username", username)));
    return new AppUser(user);
  }
}
