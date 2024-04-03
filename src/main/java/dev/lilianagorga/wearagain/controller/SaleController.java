package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

  private final SaleService saleService;

  @Autowired
  public SaleController(SaleService saleService) {
    this.saleService = saleService;
  }

  @PostMapping
  public ResponseEntity<Sale> createSale(@RequestBody Sale sale) {
    Sale createdSale = saleService.createSale(sale);
    return ResponseEntity.ok(createdSale);
  }

  @GetMapping
  public ResponseEntity<List<Sale>> getAllSales() {
    List<Sale> sales = saleService.getAllSales();
    return ResponseEntity.ok(sales);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Sale> getSaleById(@PathVariable String id) {
    return saleService.getSaleById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}")
  public ResponseEntity<Sale> updateSale(@PathVariable String id, @RequestBody Sale saleDetails) {
    return saleService.updateSale(id, saleDetails)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteSale(@PathVariable String id) {
    return saleService.deleteSale(id)
            .map(sale -> ResponseEntity.ok().build())
            .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<Sale>> getSalesByUserId(@PathVariable String userId) {
    List<Sale> sales = saleService.getSalesByUserId(userId);
    if (sales.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(sales);
  }

  @GetMapping("/item/{itemId}")
  public ResponseEntity<List<Sale>> getSalesByItemId(@PathVariable String itemId) {
    List<Sale> sales = saleService.getSalesByItemId(itemId);
    if (sales.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(sales);
  }
}
