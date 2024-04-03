package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

  private final ItemService itemService;

  @Autowired
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @PostMapping
  public ResponseEntity<Item> createItem(@RequestBody Item item) {
    Item createdItem = itemService.createItem(item);
    return ResponseEntity.ok(createdItem);
  }

  @GetMapping
  public ResponseEntity<List<Item>> getAllItems() {
    List<Item> items = itemService.getAllItems();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Item> getItemById(@PathVariable String id) {
    Optional<Item> item = itemService.getItemById(id);
    return item.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Item> updateItem(@PathVariable String id, @RequestBody Item itemDetails) {
    Optional<Item> updatedItem = itemService.updateItem(id, itemDetails);
    return updatedItem.map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteItem(@PathVariable String id) {
    Optional<Item> item = itemService.deleteItem(id);
    if (item.isPresent()) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/search")
  public ResponseEntity<List<Item>> findByTypeAndBrand(@RequestParam String type, @RequestParam String brand) {
    List<Item> items = itemService.findByTypeAndBrand(type, brand);
    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(items);
    }
  }

  @GetMapping("/available")
  public ResponseEntity<List<Item>> findAvailableItems() {
    List<Item> items = itemService.findAvailableItems();
    return ResponseEntity.ok(items);
  }

  @GetMapping("/cheaper-than")
  public ResponseEntity<List<Item>> findItemsCheaperThan(@RequestParam("price") double price) {
    List<Item> items = itemService.findItemsCheaperThan(price);
    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(items);
    }
  }

  @GetMapping("/type/{type}")
  public ResponseEntity<List<Item>> findByType(@PathVariable String type) {
    List<Item> items = itemService.findByType(type);
    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(items);
    }
  }
}
