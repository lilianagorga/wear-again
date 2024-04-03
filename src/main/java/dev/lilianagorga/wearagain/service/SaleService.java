package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

  private final SaleRepository saleRepository;

  @Autowired
  public SaleService(SaleRepository saleRepository) {
    this.saleRepository = saleRepository;
  }

  public Sale createSale(Sale sale) {
    return saleRepository.save(sale);
  }

  public List<Sale> getAllSales() {
    return saleRepository.findAll();
  }

  public Optional<Sale> getSaleById(String id) {
    return saleRepository.findById(id);
  }

  public Optional<Sale> updateSale(String id, Sale saleDetails) {
    return saleRepository.findById(id)
            .map(existingSale -> {
              existingSale.setItemId(saleDetails.getItemId());
              existingSale.setUserId(saleDetails.getUserId());
              return saleRepository.save(existingSale);
            });
  }

  public Optional<Sale> deleteSale(String id) {
    return saleRepository.findById(id)
            .map(sale -> {
              saleRepository.delete(sale);
              return sale;
            });
  }

  public List<Sale> getSalesByUserId(String userId) {
    return saleRepository.findByUserId(userId);
  }

  public List<Sale> getSalesByItemId(String itemId) {
    return saleRepository.findByItemId(itemId);
  }
}
