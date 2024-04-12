package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemWebController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ItemWebControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ItemService itemService;

  private Item sampleItem;

  @BeforeEach
  public void setUp() {
    sampleItem = new Item("1", LocalDate.now(), "T-shirt", "Gucci", "M", 199.99, true);
  }

  @Test
  public void viewItems_ShouldAddItemsToModelAndReturnItemsView() throws Exception {
    List<Item> items = Collections.singletonList(sampleItem);
    when(itemService.getAllItems()).thenReturn(items);

    mockMvc.perform(get("/items"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("items"))
            .andExpect(model().attribute("items", items))
            .andExpect(view().name("items"));

    verify(itemService, times(1)).getAllItems();
  }

  @Test
  public void viewItemDetails_WhenItemExists_ShouldAddItemToModelAndReturnItemDetailView() throws Exception {
    when(itemService.getItemById("1")).thenReturn(Optional.of(sampleItem));

    mockMvc.perform(get("/items/1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("item"))
            .andExpect(model().attribute("item", sampleItem))
            .andExpect(view().name("item-detail"));

    verify(itemService, times(1)).getItemById("1");
  }

  @Test
  public void viewItemDetails_WhenItemDoesNotExist_ShouldShowErrorMessage() throws Exception {
    when(itemService.getItemById("1")).thenReturn(Optional.empty());

    mockMvc.perform(get("/items/1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("errorMessage"))
            .andExpect(model().attribute("errorMessage", "Item not found with ID: 1"))
            .andExpect(view().name("items"));

    verify(itemService, times(1)).getItemById("1");
  }

}
