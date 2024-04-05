package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.model.UserUpdateDTO;
import dev.lilianagorga.wearagain.repository.UserRepository;
import dev.lilianagorga.wearagain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@Controller
public class UserWebController {

  private final UserService userService;
  private final UserRepository userRepository;

  @Autowired
  public UserWebController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

  @GetMapping("/profile")
  public String userProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String username = ((UserDetails) authentication.getPrincipal()).getUsername();
      User user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
      model.addAttribute("user", user);
      return "user-profile";
    } else {
      return "redirect:/login";
    }
  }

  @GetMapping("/edit")
  public String editProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String username = ((UserDetails) authentication.getPrincipal()).getUsername();
      User user = userRepository.findByUsername(username)
              .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
      model.addAttribute("user", user);
      return "user-edit";
    } else {
      return "redirect:/login";
    }
  }

  @PostMapping("/profile/update")
  @ResponseBody
  public ResponseEntity<?> updateProfile(@RequestBody UserUpdateDTO updatedUserDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
    }

    String username = ((UserDetails)authentication.getPrincipal()).getUsername();
    User updatedUser = userService.updateUserDTO(username, updatedUserDTO)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    return ResponseEntity.ok(updatedUser);
  }

}

