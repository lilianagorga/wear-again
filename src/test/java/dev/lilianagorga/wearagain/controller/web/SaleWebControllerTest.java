package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.service.SaleService;
import dev.lilianagorga.wearagain.service.ItemService;
import dev.lilianagorga.wearagain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleWebController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class SaleWebControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SaleService saleService;
  @MockBean
  private ItemService itemService;
  @MockBean
  private UserService userService;

  private Sale sampleSale;
  private User sampleUser;

  @BeforeEach
  public void setUp() {
    sampleSale = new Sale("1", "itemId1", "userId1");
    Item sampleItem = new Item("1", LocalDate.now(), "T-shirt", "Gucci", "M", 199.99, true);
    sampleUser = new User("1", "TestName", "TestSurname", null, "TestAddress",
            "TestDocumentId", "test@example.com", "TestUsername", "TestPassword");

    when(saleService.getSaleById("1")).thenReturn(Optional.of(sampleSale));
    when(itemService.getItemById("itemId1")).thenReturn(Optional.of(sampleItem));
    when(userService.getUserById("userId1")).thenReturn(Optional.of(sampleUser));
  }

  @Test
  @WithMockUser(username = "TestUsername")
  public void getAllSales_WithAuthenticatedUser_ShouldReturnSalesHistoryPage() throws Exception {
    when(userService.getUserByUsername("TestUsername")).thenReturn(Optional.of(sampleUser));
    when(saleService.getSalesByUserId("1")).thenReturn(Collections.singletonList(sampleSale));

    mockMvc.perform(get("/sales"))
            .andExpect(status().isOk())
            .andExpect(view().name("sales-history"));

    verify(saleService).getSalesByUserId("1");
  }

  @Test
  @WithMockUser(username = "TestUsername")
  public void getSaleDetails_WithValidId_ShouldReturnDetailPage() throws Exception {
    mockMvc.perform(get("/sales/1"))
            .andExpect(status().isOk())
            .andExpect(view().name("sale-detail"));

    verify(saleService).getSaleById("1");
  }

  @Test
  @WithMockUser(username = "TestUsername")
  public void addSale_WithAuthenticatedUser_ShouldAddSale() throws Exception {
    Authentication auth = mock(Authentication.class);
    UserDetails userDetails = new org.springframework.security.core.userdetails.User("TestUsername", "TestPassword", AuthorityUtils.createAuthorityList("USER"));
    when(auth.getPrincipal()).thenReturn(userDetails);
    when(userService.getUserByUsername("TestUsername")).thenReturn(Optional.of(sampleUser));

    mockMvc.perform(post("/sales/add/1")
                    .principal(auth))
            .andExpect(status().isOk())
            .andExpect(content().string("Item added to sale successfully"));

    verify(saleService).createSale(any(Sale.class));
  }

  @Test
  @WithMockUser(username = "TestUsername")
  public void getSaleDetails_WithInvalidId_ShouldReturnNotFound() throws Exception {
    when(saleService.getSaleById("invalid_id")).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

    mockMvc.perform(get("/sales/invalid_id"))
            .andExpect(status().isNotFound());
  }

  @Test
  public void addSale_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
    mockMvc.perform(post("/sales/add/1"))
            .andExpect(status().isUnauthorized());
  }

}

