package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.model.UserUpdateDTO;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository , PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(User user) {
    return userRepository.save(user);
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Optional<User> getUserById(String id) {
    return userRepository.findById(id);
  }

  public Optional<User> updateUser(User user) {
    return userRepository.findById(user.getId())
            .map(existingUser -> {
              existingUser.setName(user.getName());
              existingUser.setSurname(user.getSurname());
              existingUser.setBirthdate(user.getBirthdate());
              existingUser.setAddress(user.getAddress());
              existingUser.setDocumentId(user.getDocumentId());
              existingUser.setEmail(user.getEmail());
              if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
              }
              return userRepository.save(existingUser);
            });
  }

  public Optional<User> updateUserDTO(String username, UserUpdateDTO userUpdateDTO) {
  return userRepository.findByUsername(username).map(user -> {
    if (!passwordEncoder.matches(userUpdateDTO.getPassword(), user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Current password is incorrect.");
    }

    if (userUpdateDTO.getNewPassword() != null && !userUpdateDTO.getNewPassword().isEmpty()) {
      if (!userUpdateDTO.getNewPassword().equals(userUpdateDTO.getConfirmNewPassword())) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "New passwords do not match.");
      }
      user.setPassword(passwordEncoder.encode(userUpdateDTO.getNewPassword()));
    }

    user.setEmail(userUpdateDTO.getEmail());
    user.setUsername(userUpdateDTO.getUsername());
    return userRepository.save(user);
  });
}


  public Optional<User> deleteUser(String id) {
    return userRepository.findById(id)
            .map(user -> {
              userRepository.delete(user);
              return user;
            });
  }

  public Optional<User> getUserByDocumentId(String documentId) {
    return userRepository.findByDocumentId(documentId);
  }

  public List<User> getUsersByName(String name) {
    return userRepository.findByName(name);
  }

  public Optional<User> getUserBySurnameAndName(String surname, String name) {
    return userRepository.findBySurnameAndName(surname, name);
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public Optional<User> updateUserEmail(String id, String email) {
    return userRepository.findById(id)
            .map(existingUser -> {
              existingUser.setEmail(email);
              return userRepository.save(existingUser);
            });
  }

  public User registerNewUser(User user) throws DataIntegrityViolationException {
    if (userRepository.findByUsername(user.getUsername()).isPresent() ||
            userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new DataIntegrityViolationException("Username or email already exists.");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

}
