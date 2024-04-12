package dev.lilianagorga.wearagain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void profileAccessPermittedWhenTestProfileActive() throws Exception {
    mockMvc.perform(get("/profile"))
            .andExpect(status().isOk());
  }

}
