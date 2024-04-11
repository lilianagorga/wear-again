package dev.lilianagorga.wearagain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class WearAgainApplication {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(WearAgainApplication.class);
    app.setAdditionalProfiles("ui");
    if (Arrays.asList(args).contains("--cli")) {
      app.setWebApplicationType(WebApplicationType.NONE);
      app.setAdditionalProfiles("cli");
    }
    app.run(args);
  }
}
