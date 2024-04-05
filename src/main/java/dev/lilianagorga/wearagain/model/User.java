package dev.lilianagorga.wearagain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
  @Id
  private String id;
  private String name;
  private String surname;
  private LocalDate birthdate;
  private String address;
  @Indexed(unique = true)
  private String documentId;
  @Indexed(unique = true)
  private String email;
  private String username;
  private String password;
}
