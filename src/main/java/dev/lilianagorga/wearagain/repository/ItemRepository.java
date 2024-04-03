package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ItemRepository extends MongoRepository<Item, String> {
  List<Item> findByAvailableTrue();
  List<Item> findByTypeAndBrand(String type, String brand);
}
