package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private MongoMappingContext mongoMappingContext;


  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    IndexOperations indexOps = mongoTemplate.indexOps(User.class);
    IndexResolver resolver = new MongoPersistentEntityIndexResolver(mongoMappingContext);
    resolver.resolveIndexFor(User.class).forEach(indexOps::ensureIndex);
    testUser = new User(
            null, "Maria", "Bianchi", LocalDate.of(1965, 5, 15),
            "Via Roma 1, Roma", "DOC123", "maria.bianchi@email.com",
            "maria", "12345"
    );

    userRepository.save(testUser);
  }

  @AfterEach
  void tearDown() {
    userRepository.deleteAll();
  }

  @Test
  void findByDocumentId_WhenDocumentIdExists_ShouldReturnUser() {
    Optional<User> found = userRepository.findByDocumentId(testUser.getDocumentId());
    assertThat(found).isPresent();
    assertThat(found.get().getDocumentId()).isEqualTo(testUser.getDocumentId());
  }

  @Test
  void findByName_WhenNameExists_ShouldReturnUsersList() {
    List<User> found = userRepository.findByName(testUser.getName());
    assertThat(found).hasSize(1);
    assertThat(found.getFirst().getName()).isEqualTo(testUser.getName());
  }

  @Test
  void findBySurnameAndName_WhenSurnameAndNameExist_ShouldReturnUser() {
    Optional<User> found = userRepository.findBySurnameAndName(testUser.getSurname(), testUser.getName());
    assertThat(found).isPresent();
    assertThat(found.get().getSurname()).isEqualTo(testUser.getSurname());
    assertThat(found.get().getName()).isEqualTo(testUser.getName());
  }

  @Test
  void findByEmail_WhenEmailExists_ShouldReturnUser() {
    Optional<User> found = userRepository.findByEmail(testUser.getEmail());
    assertThat(found).isPresent();
    assertThat(found.get().getEmail()).isEqualTo(testUser.getEmail());
  }

  @Test
  void findByUsername_WhenUsernameExists_ShouldReturnUser() {
    Optional<User> found = userRepository.findByUsername(testUser.getUsername());
    assertThat(found).isPresent();
    assertThat(found.get().getUsername()).isEqualTo(testUser.getUsername());
  }

  @Test
  void findByUsername_WhenUsernameDoesNotExist_ShouldReturnEmpty() {
    Optional<User> found = userRepository.findByUsername("non_existent_username");
    assertThat(found).isNotPresent();
  }

  @Test
  void deleteUser_WhenUserExists_ShouldRemoveUser() {
    userRepository.deleteById(testUser.getId());
    Optional<User> found = userRepository.findById(testUser.getId());
    assertThat(found).isNotPresent();
  }

  @Test
  void updateUser_WhenUserExists_ShouldUpdateInformation() {
    User updatedUser = new User(testUser.getId(), "UpdatedName", "UpdatedSurname", testUser.getBirthdate(),
            "UpdatedAddress", testUser.getDocumentId(), testUser.getEmail(),
            testUser.getUsername(), testUser.getPassword());
    userRepository.save(updatedUser);

    Optional<User> found = userRepository.findById(testUser.getId());
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("UpdatedName");
  }

  @Test
  void saveUser_WithDuplicateEmail_ShouldThrowException() {
    User newUser = new User(null, "NewName", "NewSurname", LocalDate.now(),
            "New Address", "NewDOCID", testUser.getEmail(),
            "newUsername", "newPassword");

    assertThrows(DuplicateKeyException.class, () -> {
      userRepository.save(newUser);
    });
  }

}

