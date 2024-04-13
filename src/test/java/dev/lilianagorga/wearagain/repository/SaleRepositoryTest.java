package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.Sale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class SaleRepositoryTest {

  @Autowired
  private SaleRepository saleRepository;

  private Sale testSale;

  @BeforeEach
  void setUp() {
    saleRepository.deleteAll();

    testSale = new Sale(
            null,
            "someItemId",
            "someUserId"
    );

    saleRepository.save(testSale);
  }

  @AfterEach
  void tearDown() {
    saleRepository.deleteAll();
  }

  @Test
  void whenSavingSale_itShouldBeAccessible() {
    Sale insertedSale = saleRepository.save(new Sale(null, "newItemId", "newUserId"));
    Optional<Sale> retrievedSale = saleRepository.findById(insertedSale.getId());
    assertThat(retrievedSale).isPresent().hasValueSatisfying(sale -> {
      assertThat(sale.getItemId()).isEqualTo("newItemId");
      assertThat(sale.getUserId()).isEqualTo("newUserId");
    });
  }

  @Test
  void whenUpdatingSale_theChangesShouldBeReflected() {
    testSale.setUserId("updatedUserId");
    saleRepository.save(testSale);
    Optional<Sale> updatedSale = saleRepository.findById(testSale.getId());
    assertThat(updatedSale).isPresent().hasValueSatisfying(sale ->
            assertThat(sale.getUserId()).isEqualTo("updatedUserId")
    );
  }

  @Test
  void whenDeletingSale_itShouldNoLongerExist() {
    saleRepository.delete(testSale);
    Optional<Sale> deletedSale = saleRepository.findById(testSale.getId());
    assertThat(deletedSale).isEmpty();
  }

  @Test
  void findByUserId_shouldReturnSalesForGivenUser() {
    List<Sale> userSales = saleRepository.findByUserId(testSale.getUserId());
    assertThat(userSales).containsExactly(testSale);
  }

  @Test
  void findByItemId_shouldReturnSalesForGivenItem() {
    List<Sale> itemSales = saleRepository.findByItemId(testSale.getItemId());
    assertThat(itemSales).containsExactly(testSale);
  }

  @Test
  void findByUserId_WhenNoSales_ShouldReturnEmptyList() {
    List<Sale> userSales = saleRepository.findByUserId("nonExistingUserId");
    assertThat(userSales).isEmpty();
  }

  @Test
  void findByItemId_WhenNoSales_ShouldReturnEmptyList() {
    List<Sale> itemSales = saleRepository.findByItemId("nonExistingItemId");
    assertThat(itemSales).isEmpty();
  }

  @Test
  void whenSavingMultipleSales_ConcurrentUpdate_ShouldNotAffectRetrieval() throws InterruptedException {
    Sale anotherSale = new Sale(null, "someItemId", "anotherUserId");
    Sale savedSale = saleRepository.save(anotherSale);

    Thread thread = new Thread(() -> {
      saleRepository.findById(savedSale.getId()).ifPresent(concurrentSale -> {
        concurrentSale.setUserId("updatedInThread");
        saleRepository.save(concurrentSale);
      });
    });
    thread.start();
    thread.join();

    Optional<Sale> retrievedSale = saleRepository.findById(savedSale.getId());
    assertThat(retrievedSale).isPresent();
    assertThat(retrievedSale.get().getUserId()).isEqualTo("updatedInThread");
  }
}

