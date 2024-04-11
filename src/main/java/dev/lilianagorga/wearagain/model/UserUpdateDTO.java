package dev.lilianagorga.wearagain.model;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
  private String email;
  private String username;
  private String password;
  private String newPassword;
  private String confirmNewPassword;
}

