package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class HomeController {

  @RequestMapping("/")
  public String home(HttpServletRequest request) {
    System.out.println("Request URL: " + request.getRequestURL().toString());
    return "home";
  }
  @RequestMapping("/login")
  public String login() {
    return "login";
  }

  @RequestMapping("user")
  @ResponseBody
  public Object user(@AuthenticationPrincipal OAuth2User oauth2User, @AuthenticationPrincipal User user) {
    if (oauth2User != null) {
      return oauth2User;
    } else if (user != null) {
      return user;
    } else {
      return "No user authenticated";
    }
  }
}
