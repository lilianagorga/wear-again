package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
  List<Item> findByAvailableTrue();
  List<Item> findByTypeAndBrand(String type, String brand);

  @Query("{ 'price' : { $lt: ?0 } }")
  List<Item> findItemsCheaperThan(double price);

  List<Item> findByType(String type);
}
