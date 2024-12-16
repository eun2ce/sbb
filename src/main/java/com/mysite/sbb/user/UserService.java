package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public SiteUser create(String username, String email, String password) {
    SiteUser user = new SiteUser();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    this.userRepository.save(user);
    return user;
  }

  public SiteUser update(SiteUser siteUser, String password) {
    siteUser.updatePassword(passwordEncoder.encode(password));
    this.userRepository.save(siteUser);
    return siteUser;
  }

  public SiteUser getUser(String username) {
    return this.userRepository.findByUsername(username)
        .orElseThrow(() -> new DataNotFoundException("siteuser not found"));
  }

  public SiteUser getUserByEmail(String email) {
    return this.userRepository.findByEmail(email)
        .orElseThrow(() -> new DataNotFoundException("siteuser not found"));
  }

  public boolean isMatch(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
