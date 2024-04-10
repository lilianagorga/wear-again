package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

  private final ItemRepository itemRepository;

  @Autowired
  public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  public Item createItem(Item item) {
    return itemRepository.save(item);
  }

  public List<Item> getAllItems() {
    return itemRepository.findAll();
  }

  public Optional<Item> getItemById(String id) {
    return itemRepository.findById(id);
  }

  public Optional<Item> updateItem(String id, Item itemDetails) {
    return itemRepository.findById(id)
            .map(existingItem -> {
              existingItem.setInsertDate(itemDetails.getInsertDate());
              existingItem.setType(itemDetails.getType());
              existingItem.setBrand(itemDetails.getBrand());
              existingItem.setSize(itemDetails.getSize());
              existingItem.setPrice(itemDetails.getPrice());
              existingItem.setAvailable(itemDetails.getAvailable());
              return itemRepository.save(existingItem);
            });
  }

  public Optional<Item> deleteItem(String id) {
    return itemRepository.findById(id)
            .map(item -> {
              itemRepository.delete(item);
              return item;
            });
  }

  public List<Item> findByTypeAndBrand(String type, String brand) {
    return itemRepository.findByTypeAndBrand(type, brand);
  }

  public List<Item> findAvailableItems() {
    return itemRepository.findByAvailableTrue();
  }

  public List<Item> findItemsCheaperThan(double price) {
    return itemRepository.findItemsCheaperThan(price);
  }

  public List<Item> findByType(String type) {
    return itemRepository.findByType(type);
  }

  public String formatItemDetails(Item item) {
    return String.format("ID: %s, Insert Date: %s, Type: %s, Brand: %s, Size: %s, Price: %.2f, Available: %s",
            item.getId(),
            item.getInsertDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            item.getType(),
            item.getBrand(),
            item.getSize(),
            item.getPrice(),
            item.getAvailable() ? "Yes" : "No");
  }

}
