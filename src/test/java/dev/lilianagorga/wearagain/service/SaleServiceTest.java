package dev.lilianagorga.wearagain.service;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
public class SaleServiceTest {

  @Mock
  private SaleRepository saleRepository;

  @InjectMocks
  private SaleService saleService;

  private Sale sampleSale;

  @BeforeEach
  void setUp() {
    sampleSale = new Sale("1", "itemId1", "userId1");
    Mockito.when(saleRepository.findById("1")).thenReturn(Optional.of(sampleSale));
    Mockito.when(saleRepository.save(any(Sale.class))).thenReturn(sampleSale);
  }

  @Test
  void createSale_ShouldReturnCreatedSale() {
    Sale newSale = new Sale(null, "itemId1", "userId1");
    Sale savedSale = saleService.createSale(newSale);

    assertEquals(newSale.getItemId(), savedSale.getItemId());
    verify(saleRepository, times(1)).save(newSale);
  }

  @Test
  void getAllSales_ShouldReturnAllSales() {
    List<Sale> expectedSales = Arrays.asList(sampleSale, new Sale("2", "itemId2", "userId2"));
    when(saleRepository.findAll()).thenReturn(expectedSales);

    List<Sale> actualSales = saleService.getAllSales();

    assertEquals(expectedSales, actualSales);
    verify(saleRepository, times(1)).findAll();
  }

  @Test
  void getSaleById_ShouldReturnSale() {
    Optional<Sale> foundSale = saleService.getSaleById("1");

    assertTrue(foundSale.isPresent());
    assertEquals(sampleSale, foundSale.get());
    verify(saleRepository, times(1)).findById("1");
  }

  @Test
  void updateSale_ShouldUpdateAndReturnSale() {
    Sale updatedDetails = new Sale("1", "itemId2", "userId2");
    Mockito.when(saleRepository.save(any(Sale.class))).thenReturn(updatedDetails);

    Optional<Sale> updatedSale = saleService.updateSale("1", updatedDetails);

    assertTrue(updatedSale.isPresent());
    assertEquals("itemId2", updatedSale.get().getItemId());
    assertEquals("userId2", updatedSale.get().getUserId());
    verify(saleRepository, times(1)).findById("1");
    verify(saleRepository, times(1)).save(any(Sale.class));
  }

  @Test
  void getSalesByUserId_ShouldReturnSalesList() {
    List<Sale> userSales = Arrays.asList(sampleSale, new Sale("3", "itemId3", "userId1"));
    when(saleRepository.findByUserId("userId1")).thenReturn(userSales);

    List<Sale> fetchedSales = saleService.getSalesByUserId("userId1");

    assertEquals(userSales, fetchedSales);
    verify(saleRepository, times(1)).findByUserId("userId1");
  }

  @Test
  void deleteSale_ShouldDeleteSale() {
    Optional<Sale> deletedSale = saleService.deleteSale("1");

    assertTrue(deletedSale.isPresent());
    verify(saleRepository, times(1)).delete(sampleSale);
    verify(saleRepository, times(1)).findById("1");
  }

  @Test
  void getSalesByItemId_ShouldReturnSalesList() {
    List<Sale> itemSales = Arrays.asList(sampleSale, new Sale("4", "itemId1", "userId3"));
    when(saleRepository.findByItemId("itemId1")).thenReturn(itemSales);

    List<Sale> fetchedSales = saleService.getSalesByItemId("itemId1");

    assertEquals(itemSales, fetchedSales);
    verify(saleRepository, times(1)).findByItemId("itemId1");
  }
}

