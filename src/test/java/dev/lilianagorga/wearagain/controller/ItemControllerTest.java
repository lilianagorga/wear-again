package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class ItemControllerTest {

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
  public void createItem_ShouldReturnCreatedItem() throws Exception {
    when(itemService.createItem(any(Item.class))).thenReturn(sampleItem);

    mockMvc.perform(post("/api/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"type\":\"T-shirt\",\"brand\":\"Gucci\",\"size\":\"M\",\"price\":199.99,\"available\":true}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value(sampleItem.getType()));

    verify(itemService, times(1)).createItem(any(Item.class));
  }

  @Test
  public void getAllItems_ShouldReturnAllItems() throws Exception {
    List<Item> items = Collections.singletonList(sampleItem);
    when(itemService.getAllItems()).thenReturn(items);

    mockMvc.perform(get("/api/items"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value(sampleItem.getType()));

    verify(itemService, times(1)).getAllItems();
  }

  @Test
  public void getItemById_WhenItemExists_ShouldReturnItem() throws Exception {
    when(itemService.getItemById("1")).thenReturn(Optional.of(sampleItem));

    mockMvc.perform(get("/api/items/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(sampleItem.getId()));

    verify(itemService, times(1)).getItemById("1");
  }

  @Test
  public void updateItem_WhenItemExists_ShouldReturnUpdatedItem() throws Exception {
    Item updatedItem = new Item("1", LocalDate.now(), "T-shirt", "Gucci", "L", 249.99, true);
    when(itemService.updateItem(eq("1"), any(Item.class))).thenReturn(Optional.of(updatedItem));

    mockMvc.perform(put("/api/items/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"type\":\"T-shirt\",\"brand\":\"Gucci\",\"size\":\"L\",\"price\":249.99,\"available\":true}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size").value(updatedItem.getSize()));

    verify(itemService, times(1)).updateItem(eq("1"), any(Item.class));
  }

  @Test
  public void deleteItem_WhenItemExists_ShouldReturnSuccess() throws Exception {
    when(itemService.deleteItem("1")).thenReturn(Optional.of(sampleItem));

    mockMvc.perform(delete("/api/items/1"))
            .andExpect(status().isOk());

    verify(itemService, times(1)).deleteItem("1");
  }

  @Test
  public void findByTypeAndBrand_ShouldReturnFilteredItems() throws Exception {
    List<Item> items = Collections.singletonList(sampleItem);
    when(itemService.findByTypeAndBrand("T-shirt", "Gucci")).thenReturn(items);

    mockMvc.perform(get("/api/items/search?type=T-shirt&brand=Gucci"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].brand").value(sampleItem.getBrand()));

    verify(itemService, times(1)).findByTypeAndBrand("T-shirt", "Gucci");
  }

  @Test
  public void findAvailableItems_ShouldReturnAvailableItems() throws Exception {
    List<Item> items = Collections.singletonList(sampleItem);
    when(itemService.findAvailableItems()).thenReturn(items);

    mockMvc.perform(get("/api/items/available"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].available").value(true));

    verify(itemService, times(1)).findAvailableItems();
  }

  @Test
  public void findItemsCheaperThan_ShouldReturnCheaperItems() throws Exception {
    List<Item> items = Collections.singletonList(sampleItem);
    when(itemService.findItemsCheaperThan(200.00)).thenReturn(items);

    mockMvc.perform(get("/api/items/cheaper-than?price=200.00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].price").value(sampleItem.getPrice()));

    verify(itemService, times(1)).findItemsCheaperThan(200.00);
  }

  @Test
  public void findByType_ShouldReturnItemsByType() throws Exception {
    List<Item> items = Collections.singletonList(sampleItem);
    when(itemService.findByType("T-shirt")).thenReturn(items);

    mockMvc.perform(get("/api/items/type/T-shirt"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("T-shirt"));

    verify(itemService, times(1)).findByType("T-shirt");
  }
}
