package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

  private final ItemRepository itemRepository;

  @Autowired
  public ItemController(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  @PostMapping
  public Item createItem(@RequestBody Item item) {
    return itemRepository.save(item);
  }

  @GetMapping
  public List<Item> getAllItems() {
    return itemRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Item> getItemById(@PathVariable String id) {
    return itemRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Item> updateItem(@PathVariable String id, @RequestBody Item itemDetails) {
    return itemRepository.findById(id)
            .map(existingItem -> {
              existingItem.setInsertDate(itemDetails.getInsertDate());
              existingItem.setType(itemDetails.getType());
              existingItem.setBrand(itemDetails.getBrand());
              existingItem.setSize(itemDetails.getSize());
              existingItem.setPrice(itemDetails.getPrice());
              existingItem.setAvailable(itemDetails.getAvailable());
              Item updatedItem = itemRepository.save(existingItem);
              return ResponseEntity.ok(updatedItem);
            })
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteItem(@PathVariable String id) {
    return itemRepository.findById(id)
            .map(item -> {
              itemRepository.delete(item);
              return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/search")
  public ResponseEntity<List<Item>> findByTypeAndBrand(@RequestParam String type, @RequestParam String brand) {
    List<Item> items = itemRepository.findByTypeAndBrand(type, brand);
    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(items);
    }
  }

  @GetMapping("/available")
  public List<Item> findAvailableItems() {
    return itemRepository.findByAvailableTrue();
  }

  @GetMapping("/cheaper-than")
  public ResponseEntity<List<Item>> findItemsCheaperThan(@RequestParam("price") double price) {
    List<Item> items = itemRepository.findItemsCheaperThan(price);
    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(items);
    }
  }

  @GetMapping("/type/{type}")
  public ResponseEntity<List<Item>> findByType(@PathVariable String type) {
    List<Item> items = itemRepository.findByType(type);
    if (items.isEmpty()) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(items);
    }
  }
}

