package dev.lilianagorga.wearagain.controller;

import com.jayway.jsonpath.JsonPath;
import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setUp() {
    testUser = new User("1", "TestName", "TestSurname", LocalDate.of(1990, 1, 1),
            "TestAddress", "TestDocumentId", "test@example.com",
            "TestUsername", "TestPassword");
  }

  @Test
  public void createUser_ShouldReturnCreatedUser() throws Exception {
    when(userService.createUser(any())).thenReturn(testUser);

    mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"TestName\",\"surname\":\"TestSurname\",\"email\":\"test@example.com\",\"username\":\"TestUsername\",\"password\":\"TestPassword\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(testUser.getName()));

    verify(userService, times(1)).createUser(any());
  }

  @Test
  public void getAllUsers_ShouldReturnAllUsers() throws Exception {
    when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

    mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(result -> assertEquals(1,
                    JsonPath.parse(result.getResponse().getContentAsString()).read("$", List.class).size()))
            .andExpect(jsonPath("$[0].name").value(testUser.getName()));

    verify(userService, times(1)).getAllUsers();
  }


  @Test
  public void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
    when(userService.getUserById("1")).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId()));

    verify(userService, times(1)).getUserById("1");
  }

  @Test
  public void updateUser_ShouldReturnUpdatedUser() throws Exception {
    User updatedUser = new User(testUser.getId(), "UpdatedName", "UpdatedSurname", testUser.getBirthdate(),
            testUser.getAddress(), testUser.getDocumentId(), "updated@example.com",
            "UpdatedUsername", "UpdatedPassword");
    when(userService.updateUser(any(User.class))).thenReturn(Optional.of(updatedUser));

    mockMvc.perform(put("/api/users/{id}", testUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"UpdatedName\",\"surname\":\"UpdatedSurname\",\"email\":\"updated@example.com\",\"username\":\"UpdatedUsername\",\"password\":\"UpdatedPassword\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("UpdatedName"));

    verify(userService, times(1)).updateUser(any(User.class));
  }

  @Test
  public void deleteUser_ShouldReturnSuccess() throws Exception {
    when(userService.deleteUser(anyString())).thenReturn(Optional.of(testUser));

    mockMvc.perform(delete("/api/users/{id}", testUser.getId()))
            .andExpect(status().isOk());

    verify(userService, times(1)).deleteUser(anyString());
  }

  @Test
  public void getUserByDocumentId_ShouldReturnUser() throws Exception {
    when(userService.getUserByDocumentId(anyString())).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/api/users/documentId/{documentId}", testUser.getDocumentId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.documentId").value(testUser.getDocumentId()));

    verify(userService, times(1)).getUserByDocumentId(anyString());
  }

  @Test
  public void getUsersByName_ShouldReturnUsersList() throws Exception {
    when(userService.getUsersByName(anyString())).thenReturn(Collections.singletonList(testUser));

    mockMvc.perform(get("/api/users/name/{name}", testUser.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value(testUser.getName()));

    verify(userService, times(1)).getUsersByName(anyString());
  }

  @Test
  public void getUserBySurnameAndName_ShouldReturnUser() throws Exception {
    when(userService.getUserBySurnameAndName(anyString(), anyString())).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/api/users/surname/{surname}/name/{name}", testUser.getSurname(), testUser.getName()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.surname").value(testUser.getSurname()))
            .andExpect(jsonPath("$.name").value(testUser.getName()));

    verify(userService, times(1)).getUserBySurnameAndName(anyString(), anyString());
  }

  @Test
  public void getUserByEmail_ShouldReturnUser() throws Exception {
    when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(testUser));

    mockMvc.perform(get("/api/users/email/{email}", testUser.getEmail()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(testUser.getEmail()));

    verify(userService, times(1)).getUserByEmail(anyString());
  }

  @Test
  public void updateUserEmail_ShouldReturnUpdatedEmail() throws Exception {
    when(userService.updateUserEmail(anyString(), anyString())).thenReturn(Optional.of(testUser));

    mockMvc.perform(patch("/api/users/{id}/email", testUser.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"email\":\"updatedemail@example.com\"}"))
            .andExpect(status().isOk());

    verify(userService, times(1)).updateUserEmail(anyString(), anyString());
  }

  @Test
  public void registerNewUser_ShouldReturnCreatedUser() throws Exception {
    User newUser = new User("2", "NewUser", "NewSurname", null,
            "NewAddress", "NewDocumentId", "newuser@example.com",
            "NewUsername", "NewPassword");
    when(userService.registerNewUser(any(User.class))).thenReturn(newUser);

    mockMvc.perform(post("/api/users/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"name\":\"NewUser\",\"surname\":\"NewSurname\",\"email\":\"newuser@example.com\",\"username\":\"NewUsername\",\"password\":\"NewPassword\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("newuser@example.com"));

    verify(userService, times(1)).registerNewUser(any(User.class));
  }

}

