package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

  User findByDocumentId(String documentId);
}
