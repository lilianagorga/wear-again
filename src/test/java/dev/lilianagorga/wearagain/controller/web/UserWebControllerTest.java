package dev.lilianagorga.wearagain.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.model.UserUpdateDTO;
import dev.lilianagorga.wearagain.service.UserService;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserWebController.class)
@ActiveProfiles("test")
public class UserWebControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private User testUser;

  @BeforeEach
  public void setUp() {

    testUser = new User("1", "TestName", "TestSurname", null, "TestAddress",
            "TestDocumentId", "test@example.com", "TestUsername", "TestPassword");
  }

  @Test
  @WithMockUser(username = "TestUsername")
  public void userProfile_WhenAuthenticated_ShouldAddUserToModel() throws Exception {
    when(userRepository.findByUsername("TestUsername")).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/profile"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("user"))
            .andExpect(view().name("user-profile"));

    verify(userRepository, times(1)).findByUsername("TestUsername");
  }

  @Test
  @WithMockUser(username = "TestUsername", password = "TestPassword")
  public void updateProfile_WhenAuthenticated_ShouldUpdateUser() throws Exception {
    UserUpdateDTO updateDTO = new UserUpdateDTO("updatedemail@example.com", "TestUsername", "TestPassword", "newPassword", "newPassword");

    when(userService.updateUserDTO(eq("TestUsername"), any(UserUpdateDTO.class)))
            .thenReturn(Optional.of(testUser));

    mockMvc.perform(post("/profile/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("TestUsername"));

    verify(userService, times(1)).updateUserDTO(eq("TestUsername"), any(UserUpdateDTO.class));
  }

  @Test
  public void showRegistrationForm_ShouldReturnRegistrationPage() throws Exception {
    mockMvc.perform(get("/register"))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("user"));
  }

  @Test
  public void registerUser_WhenDetailsMatch_ShouldRedirectToLogin() throws Exception {
    when(userService.registerNewUser(any(User.class))).thenReturn(testUser);

    mockMvc.perform(post("/register")
                    .param("password", "TestPassword")
                    .param("confirmPassword", "TestPassword")
                    .flashAttr("user", testUser))
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/login"));

    verify(userService, times(1)).registerNewUser(any(User.class));
  }

  @Test
  public void userProfile_WhenNotAuthenticated_ShouldRedirectToLogin() throws Exception {
    mockMvc.perform(get("/profile"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
  }

  @Test
  public void registerUser_PasswordMismatch_ShouldReturnRegistrationPageWithError() throws Exception {
    mockMvc.perform(post("/register")
                    .param("password", "TestPassword")
                    .param("confirmPassword", "TestPassword1")
                    .flashAttr("user", testUser))
            .andExpect(status().isOk())
            .andExpect(view().name("register"))
            .andExpect(model().attributeExists("registrationError"));
  }

  @Test
  @WithMockUser(username = "TestUsername", password = "TestPassword")
  public void updateProfile_WhenServerException_ShouldReturnServerError() throws Exception {
    UserUpdateDTO updateDTO = new UserUpdateDTO("updatedemail@example.com", "TestUsername", "TestPassword", "newPassword", "newPassword");

    when(userService.updateUserDTO(eq("TestUsername"), any(UserUpdateDTO.class)))
            .thenThrow(new RuntimeException("Internal server error"));

    mockMvc.perform(post("/profile/update")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(updateDTO)))
            .andExpect(status().isInternalServerError());
  }
}