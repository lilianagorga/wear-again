package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class ItemServiceTest {

  @Mock
  private ItemRepository itemRepository;

  @InjectMocks
  private ItemService itemService;

  private Item sampleItem;

  @BeforeEach
  void setUp() {
    sampleItem = new Item("1", LocalDate.now(), "T-shirt", "Gucci", "M", 199.99, true);
    Mockito.when(itemRepository.findById("1")).thenReturn(Optional.of(sampleItem));
    Mockito.when(itemRepository.save(any(Item.class))).thenReturn(sampleItem);
  }

  @Test
  void createItem_ShouldReturnCreatedItem() {
    Item newItem = new Item(null, LocalDate.now(), "T-shirt", "Gucci", "M", 199.99, true);
    Item savedItem = itemService.createItem(newItem);

    assertEquals(newItem.getType(), savedItem.getType());
    verify(itemRepository, times(1)).save(newItem);
  }

  @Test
  void getAllItems_ShouldReturnAllItems() {
    List<Item> expectedItems = Arrays.asList(sampleItem, new Item("2", LocalDate.now(), "Jeans", "Levi's", "L", 89.99, false));
    when(itemRepository.findAll()).thenReturn(expectedItems);

    List<Item> actualItems = itemService.getAllItems();

    assertEquals(expectedItems, actualItems);
    verify(itemRepository, times(1)).findAll();
  }

  @Test
  void getItemById_WhenItemExists_ShouldReturnItem() {
    Optional<Item> actualItem = itemService.getItemById("1");

    assertTrue(actualItem.isPresent());
    assertEquals(sampleItem, actualItem.get());
    verify(itemRepository, times(1)).findById("1");
  }

  @Test
  void updateItem_WhenItemExists_ShouldUpdateItem() {
    Item updatedDetails = new Item("1", LocalDate.now(), "T-shirt", "Gucci", "L", 249.99, true);
    when(itemRepository.save(any(Item.class))).thenReturn(updatedDetails);

    Optional<Item> updatedItem = itemService.updateItem("1", updatedDetails);

    assertTrue(updatedItem.isPresent());
    assertEquals(updatedDetails.getSize(), updatedItem.get().getSize());
    assertEquals(updatedDetails.getPrice(), updatedItem.get().getPrice());
    verify(itemRepository, times(1)).findById("1");
    verify(itemRepository, times(1)).save(updatedDetails);
  }

  @Test
  void deleteItem_WhenItemExists_ShouldDeleteItem() {
    Optional<Item> deletedItem = itemService.deleteItem("1");

    assertTrue(deletedItem.isPresent());
    assertEquals(sampleItem, deletedItem.get());
    verify(itemRepository, times(1)).delete(sampleItem);
  }

  @Test
  void findAvailableItems_ShouldReturnAvailableItems() {
    List<Item> availableItems = Collections.singletonList(sampleItem);
    when(itemRepository.findByAvailableTrue()).thenReturn(availableItems);

    List<Item> actualItems = itemService.findAvailableItems();

    assertEquals(availableItems, actualItems);
    verify(itemRepository, times(1)).findByAvailableTrue();
  }

  @Test
  void findItemsCheaperThan_ShouldReturnCheaperItems() {
    List<Item> cheaperItems = Collections.singletonList(sampleItem);
    when(itemRepository.findItemsCheaperThan(200.00)).thenReturn(cheaperItems);

    List<Item> actualItems = itemService.findItemsCheaperThan(200.00);

    assertEquals(cheaperItems, actualItems);
    verify(itemRepository, times(1)).findItemsCheaperThan(200.00);
  }

  @Test
  void findByType_ShouldReturnItemsByType() {
    List<Item> typeItems = Collections.singletonList(sampleItem);
    when(itemRepository.findByType("T-shirt")).thenReturn(typeItems);

    List<Item> actualItems = itemService.findByType("T-shirt");

    assertEquals(typeItems, actualItems);
    verify(itemRepository, times(1)).findByType("T-shirt");
  }

  @Test
  void findByTypeAndBrand_ShouldReturnItemsFilteredByTypeAndBrand() {
    List<Item> filteredItems = Collections.singletonList(sampleItem);
    when(itemRepository.findByTypeAndBrand("T-shirt", "Gucci")).thenReturn(filteredItems);

    List<Item> actualItems = itemService.findByTypeAndBrand("T-shirt", "Gucci");

    assertEquals(filteredItems, actualItems);
    verify(itemRepository, times(1)).findByTypeAndBrand("T-shirt", "Gucci");
  }

  @Test
  void formatItemDetails_ShouldReturnFormattedString() {
    Item itemToFormat = new Item("1", LocalDate.of(2021, 4, 21), "T-shirt", "Gucci", "M", 199.99, true);
    String expectedFormat = "ID: 1, Insert Date: 21/04/2021, Type: T-shirt, Brand: Gucci, Size: M, Price: 199.99, Available: Yes";

    String actualFormat = itemService.formatItemDetails(itemToFormat);

    assertEquals(expectedFormat, actualFormat);
  }

}
