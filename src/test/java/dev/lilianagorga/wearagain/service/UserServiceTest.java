package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.model.UserUpdateDTO;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

  @MockBean
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @MockBean
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  void setUp() {
    User user = new User(
            "1", "TestName", "TestSurname", LocalDate.of(1990, 1, 1),
            "TestAddress", "TestDocumentId", "newEmail@example.com",
            "TestUsername", "TestPassword"
    );

    Mockito.when(userRepository.findByUsername("TestUsername")).thenReturn(Optional.of(user));
    Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(user));
    Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
    Mockito.when(passwordEncoder.encode("TestPassword")).thenReturn("TestPassword");
    Mockito.when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
  }



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

  @Test
  void updateUser_WhenUserExists_ShouldUpdateUser() {
    User existingUser = new User("1", "OldName", "OldSurname", null, "OldAddress", "DocId", "old@example.com", "username", "password");
    User updateUser = new User("1", "NewName", "NewSurname", null, "NewAddress", "DocId", "new@example.com", "username", "password");
    User updatedUser = new User("1", "NewName", "NewSurname", null, "NewAddress", "DocId", "new@example.com", "username", "password");

    when(userRepository.findById("1")).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    Optional<User> actualUser = userService.updateUser(updateUser);

    assertTrue(actualUser.isPresent());
    assertEquals("NewName", actualUser.get().getName());
    assertEquals("NewSurname", actualUser.get().getSurname());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void updateUserDTO_WhenPasswordMatches_ShouldUpdateUser() {
    String rawCurrentPassword = "TestPassword";
    User existingUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");
    UserUpdateDTO dto = new UserUpdateDTO(rawCurrentPassword, "newEmail@example.com", "newUsername", "newPassword", "newPassword");
    Mockito.when(userRepository.findByUsername("TestUsername")).thenReturn(Optional.of(existingUser));
    Optional<User> actualUser = userService.updateUserDTO("TestUsername", dto);
    assertTrue(actualUser.isPresent());
    assertEquals("newEmail@example.com", actualUser.get().getEmail());
    Mockito.verify(userRepository, Mockito.times(1)).save(any(User.class));
  }

  @Test
  void deleteUser_WhenUserExists_ShouldDeleteUser() {
    User user = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");

    when(userRepository.findById("1")).thenReturn(Optional.of(user));
    doNothing().when(userRepository).delete(user);

    Optional<User> actualUser = userService.deleteUser("1");

    assertTrue(actualUser.isPresent());
    assertEquals("1", actualUser.get().getId());
    verify(userRepository, times(1)).delete(user);
  }

  @Test
  void getUserByDocumentId_WhenExists_ShouldReturnUser() {
    User expectedUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");

    when(userRepository.findByDocumentId("TestDocumentId")).thenReturn(Optional.of(expectedUser));

    Optional<User> actualUser = userService.getUserByDocumentId("TestDocumentId");

    assertTrue(actualUser.isPresent());
    assertEquals("TestDocumentId", actualUser.get().getDocumentId());
    verify(userRepository, times(1)).findByDocumentId("TestDocumentId");
  }

  @Test
  void getUsersByName_ShouldReturnUsers() {
    User user1 = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");
    User user2 = new User("2", "Name", "Surname2", null, "Address2", "TestDocumentId2", "email2@example.com", "TestUsername2", "TestPassword2");
    List<User> expectedUsers = Arrays.asList(user1, user2);

    when(userRepository.findByName("Name")).thenReturn(expectedUsers);

    List<User> actualUsers = userService.getUsersByName("Name");

    assertEquals(expectedUsers, actualUsers);
    verify(userRepository, times(1)).findByName("Name");
  }

  @Test
  void getUserBySurnameAndName_WhenExists_ShouldReturnUser() {
    User expectedUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");

    when(userRepository.findBySurnameAndName("Surname", "Name")).thenReturn(Optional.of(expectedUser));

    Optional<User> actualUser = userService.getUserBySurnameAndName("Surname", "Name");

    assertTrue(actualUser.isPresent());
    assertEquals(expectedUser, actualUser.get());
    verify(userRepository, times(1)).findBySurnameAndName("Surname", "Name");
  }

  @Test
  void getUserByEmail_WhenExists_ShouldReturnUser() {
    User expectedUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");

    when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.of(expectedUser));

    Optional<User> actualUser = userService.getUserByEmail("email@example.com");

    assertTrue(actualUser.isPresent());
    assertEquals(expectedUser, actualUser.get());
    verify(userRepository, times(1)).findByEmail("email@example.com");
  }

  @Test
  void updateUserEmail_ShouldUpdateEmail() {
    User existingUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");
    User updatedUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "newEmail@example.com", "TestUsername", "TestPassword");

    when(userRepository.findById("1")).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(User.class))).thenReturn(updatedUser);

    Optional<User> actualUser = userService.updateUserEmail("1", "newEmail@example.com");

    assertTrue(actualUser.isPresent());
    assertEquals("newEmail@example.com", actualUser.get().getEmail());
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void registerNewUser_ShouldSaveUser() {
    User newUser = new User(null, "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");
    User savedUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "encodedPassword");

    when(userRepository.findByUsername("TestUsername")).thenReturn(Optional.empty());
    when(userRepository.findByEmail("email@example.com")).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(savedUser);
    when(passwordEncoder.encode("TestPassword")).thenReturn("encodedPassword");

    User actualUser = userService.registerNewUser(newUser);

    assertEquals(savedUser, actualUser);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  void getUserByUsername_WhenExists_ShouldReturnUser() {
    User expectedUser = new User("1", "Name", "Surname", null, "Address", "TestDocumentId", "email@example.com", "TestUsername", "TestPassword");

    when(userRepository.findByUsername("TestUsername")).thenReturn(Optional.of(expectedUser));

    Optional<User> actualUser = userService.getUserByUsername("TestUsername");

    assertTrue(actualUser.isPresent());
    assertEquals(expectedUser, actualUser.get());
    verify(userRepository, times(1)).findByUsername("TestUsername");
  }
}
