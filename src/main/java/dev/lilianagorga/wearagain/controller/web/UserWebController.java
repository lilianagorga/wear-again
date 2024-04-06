package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.model.UserUpdateDTO;
import dev.lilianagorga.wearagain.repository.UserRepository;
import dev.lilianagorga.wearagain.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Controller
public class UserWebController {

  private final UserService userService;
  private final UserRepository userRepository;

  @Autowired
  public UserWebController(UserService userService, UserRepository userRepository) {
    this.userService = userService;
    this.userRepository = userRepository;
  }

//  @GetMapping("/profile")
//  public String userProfile(Model model) {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    if (authentication != null && authentication.isAuthenticated()) {
//      String username = null;
//
//      if (authentication.getPrincipal() instanceof UserDetails) {
//        username = ((UserDetails) authentication.getPrincipal()).getUsername();
//      } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
//        username = ((DefaultOidcUser) authentication.getPrincipal()).getEmail();
//      }
//
//      if (username != null) {
//        Optional<User> user = userRepository.findByUsername(username);
//        if (user.isPresent()) {
//          model.addAttribute("user", user.get());
//          return "user-profile";
//        } else {
//          return "redirect:/register";
//        }
//      }
//    }
//    return "redirect:/login";
//  }

  @GetMapping("/profile")
  public String userProfile(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      String username = null;
      if (authentication.getPrincipal() instanceof UserDetails) {
        username = ((UserDetails) authentication.getPrincipal()).getUsername();
      } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
        username = ((DefaultOidcUser) authentication.getPrincipal()).getEmail();
      }

      if (username != null) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
          model.addAttribute("user", userOpt.get());
        } else {
          model.addAttribute("showModal", true);
        }
      }
      return "user-profile";
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

  @GetMapping("/register")
  public String showRegistrationForm(Model model) {
    model.addAttribute("user", new User());
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@ModelAttribute User user,
                             @RequestParam String confirmPassword,
                             Model model) {
    if (!user.getPassword().equals(confirmPassword)) {
      model.addAttribute("user", user);
      model.addAttribute("registrationError", "Passwords do not match.");
      return "register";
    }
    try {
      userService.registerNewUser(user);
      return "redirect:/login";
    } catch (DataIntegrityViolationException e) {
      model.addAttribute("user", user);
      model.addAttribute("registrationError", "Username or email already exists.");
      return "register";
    } catch (Exception e) {
      model.addAttribute("user", user);
      model.addAttribute("registrationError", "An error occurred during registration.");
      return "register";
    }
  }

}

