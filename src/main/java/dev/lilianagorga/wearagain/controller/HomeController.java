package dev.lilianagorga.wearagain.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
  public Principal user(@AuthenticationPrincipal UserDetails principal) {
    return (Principal) principal;
  }
}
