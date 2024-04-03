package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.createUser(user);
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable String id) {
    return userService.getUserById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User userDetails) {
    return userService.updateUser(id, userDetails)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteUser(@PathVariable String id) {
    return userService.deleteUser(id)
            .map(user -> ResponseEntity.ok().build())
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/documentId/{documentId}")
  public ResponseEntity<User> getUserByDocumentId(@PathVariable String documentId) {
    return userService.getUserByDocumentId(documentId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<List<User>> getUsersByName(@PathVariable String name) {
    List<User> users = userService.getUsersByName(name);
    if (users.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(users);
    }
  }

  @GetMapping("/surname/{surname}/name/{name}")
  public ResponseEntity<User> getUserBySurnameAndName(@PathVariable String surname, @PathVariable String name) {
    return userService.getUserBySurnameAndName(surname, name)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }
}

