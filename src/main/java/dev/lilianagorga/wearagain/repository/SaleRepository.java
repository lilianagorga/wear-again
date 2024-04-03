package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SaleRepository extends MongoRepository<Sale, String> {
  List<Sale> findByUserId(String userId);
  List<Sale> findByItemId(String itemId);
}
