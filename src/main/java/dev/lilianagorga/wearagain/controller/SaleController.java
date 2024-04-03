package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

  private final SaleRepository saleRepository;

  @Autowired
  public SaleController(SaleRepository saleRepository) {
    this.saleRepository = saleRepository;
  }

  @PostMapping
  public Sale createSale(@RequestBody Sale sale) {
    return saleRepository.save(sale);
  }

  @GetMapping
  public List<Sale> getAllSales() {
    return saleRepository.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Sale> getSaleById(@PathVariable String id) {
    return saleRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Sale> updateSale(@PathVariable String id, @RequestBody Sale saleDetails) {
    return saleRepository.findById(id)
            .map(existingSale -> {
              existingSale.setItemId(saleDetails.getItemId());
              existingSale.setUserId(saleDetails.getUserId());
              Sale updatedSale = saleRepository.save(existingSale);
              return ResponseEntity.ok(updatedSale);
            })
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteSale(@PathVariable String id) {
    return saleRepository.findById(id)
            .map(sale -> {
              saleRepository.delete(sale);
              return ResponseEntity.ok().build();
            })
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/user/{userId}")
  public List<Sale> getSalesByUserId(@PathVariable String userId) {
    return saleRepository.findByUserId(userId);
  }

  @GetMapping("/items/{itemId}")
  public List<Sale> getSalesByItemId(@PathVariable String itemId) {
    return saleRepository.findByItemId(itemId);
  }

}

