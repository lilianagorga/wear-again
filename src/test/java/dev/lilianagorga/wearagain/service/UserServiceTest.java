package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

  @Mock
  private UserRepository userRepository;


  @InjectMocks
  private UserService userService;

  @Test
  void getAllUsers_ShouldReturnUsers() {
    User user1 = new User("1", "Name", "Surname", null, "Address", "DocId", "email@example.com", "username", "password");
    User user2 = new User("2", "Name2", "Surname2", null, "Address2", "DocId2", "email2@example.com", "username2", "password2");
    List<User> expectedUsers = Arrays.asList(user1, user2);

    when(userRepository.findAll()).thenReturn(expectedUsers);

    List<User> actualUsers = userService.getAllUsers();

    assertEquals(expectedUsers, actualUsers);
    verify(userRepository, times(1)).findAll();
  }

  @Test
  void createUser_ShouldReturnCreatedUser() {
    User newUser = new User(null, "Name", "Surname", null, "Address", "DocId", "email@example.com", "username", "password");
    User savedUser = new User("1", "Name", "Surname", null, "Address", "DocId", "email@example.com", "username", "password");

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    User actualUser = userService.createUser(newUser);

    assertEquals(savedUser, actualUser);
    verify(userRepository, times(1)).save(newUser);
  }

  @Test
  void getUserById_WhenUserExists_ShouldReturnUser() {
    Optional<User> expectedUser = Optional.of(new User("1", "Name", "Surname", null, "Address", "DocId", "email@example.com", "username", "password"));

    when(userRepository.findById("1")).thenReturn(expectedUser);

    Optional<User> actualUser = userService.getUserById("1");

    assertEquals(expectedUser, actualUser);
    verify(userRepository, times(1)).findById("1");
  }

}
