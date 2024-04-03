package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
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

  public Optional<User> updateUser(String id, User userDetails) {
    return userRepository.findById(id)
            .map(existingUser -> {
              existingUser.setName(userDetails.getName());
              existingUser.setSurname(userDetails.getSurname());
              existingUser.setBirthdate(userDetails.getBirthdate());
              existingUser.setAddress(userDetails.getAddress());
              existingUser.setDocumentId(userDetails.getDocumentId());
              return userRepository.save(existingUser);
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
}
