package dev.lilianagorga.wearagain.service;


import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User user = super.loadUser(userRequest);
    return processOAuth2User(user);
  }

  private OAuth2User processOAuth2User(OAuth2User oAuth2User) {
    String email = oAuth2User.getAttribute("email");
    Optional<User> userOptional = userRepository.findByEmail(email);
    User user;


    if (userOptional.isEmpty()) {
      user = new User();
      user.setEmail(email);
      user.setName(oAuth2User.getAttribute("name"));
      userRepository.save(user);
    } else {
      user = userOptional.get();
      boolean needsUpdate = false;
      String name = oAuth2User.getAttribute("name");
      if (name != null && !name.equals(user.getName())) {
        user.setName(name);
        needsUpdate = true;
      }
      if (needsUpdate) {
        userRepository.save(user);
      }
    }
    Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
    attributes.put("customUser", user);

    return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "email");
  }
}

