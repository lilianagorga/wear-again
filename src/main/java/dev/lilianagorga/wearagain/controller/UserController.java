package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserRepository userRepository;

  @Autowired
  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping
  public User createUser(@RequestBody User user) {
    return userRepository.save(user);
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable String id) {
    return userRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
    return userRepository.findById(id)
            .map(existingUser -> {
              existingUser.setName(userDetails.getName());
              existingUser.setSurname(userDetails.getSurname());
              existingUser.setBirthdate(userDetails.getBirthdate());
              existingUser.setAddress(userDetails.getAddress());
              existingUser.setDocumentId(userDetails.getDocumentId());
              User updatedUser = userRepository.save(existingUser);
              return ResponseEntity.ok(updatedUser);
            })
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable String id) {
    return userRepository.findById(id)
            .map(user -> {
              userRepository.delete(user);
              return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/documentId/{documentId}")
  public ResponseEntity<User> getUserByDocumentId(@PathVariable String documentId) {
    return userRepository.findByDocumentId(documentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<List<User>> getUsersByName(@PathVariable String name) {
    List<User> users = userRepository.findByName(name);
    if(users.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(users);
    }
  }


  @GetMapping("/surname/{surname}/name/{name}")
  public ResponseEntity<User> getUserBySurnameAndName(@PathVariable String surname, @PathVariable String name) {
    return userRepository.findBySurnameAndName(surname, name)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }


}

