package dev.lilianagorga.wearagain.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDTO {
  private String email;
  private String username;
  private String password;
  private String newPassword;
  private String confirmNewPassword;
}

