package dev.lilianagorga.wearagain.controller.web;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
public class HomeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testHomePage() throws Exception {
    mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("home"));
  }

  @Test
  public void testLoginPage() throws Exception {
    mockMvc.perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
  }

  @Test
  @WithMockUser(username = "user1")
  public void testUserPageWithUser() throws Exception {
    mockMvc.perform(get("/user"))
            .andExpect(status().isOk())
            .andExpect(authenticated().withUsername("user1"))
            .andExpect(content().string("No user authenticated"));
  }

  @Test
  public void testUserPageWithOAuth2User() throws Exception {
    OAuth2User principal = mock(OAuth2User.class);
    mockMvc.perform(get("/user").with(authentication(new TestingAuthenticationToken(principal, null))))
            .andExpect(status().isOk())
            .andExpect(content().string("No user authenticated"));
  }
}
