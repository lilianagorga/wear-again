package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
  Optional<User> findByDocumentId(String documentId);
  List<User> findByName(String name);
  Optional<User> findBySurnameAndName(String surname, String name);

  Optional<User> findByEmail(String email);

  Optional<User> findByUsername(String username);
}
