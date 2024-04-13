package dev.lilianagorga.wearagain.repository;

import dev.lilianagorga.wearagain.model.Item;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataMongoTest
@ActiveProfiles("test")
public class ItemRepositoryTest {

  @Autowired
  private ItemRepository itemRepository;

  private Item testItem;

  @BeforeEach
  void setUp() {
    itemRepository.deleteAll();

    testItem = new Item(
            null,
            LocalDate.now(),
            "Sweater",
            "H&M",
            "M",
            29.99,
            true
    );

    itemRepository.save(testItem);
  }

  @AfterEach
  void tearDown() {
    itemRepository.deleteAll();
  }

  @Test
  void findByAvailableTrue_ShouldReturnAvailableItems() {
    List<Item> foundItems = itemRepository.findByAvailableTrue();
    assertThat(foundItems).isNotEmpty();
    assertThat(foundItems.getFirst().getAvailable()).isTrue();
  }

  @Test
  void findByTypeAndBrand_WhenTypeAndBrandExist_ShouldReturnItemList() {
    List<Item> foundItems = itemRepository.findByTypeAndBrand(testItem.getType(), testItem.getBrand());
    assertThat(foundItems).isNotEmpty();
    assertThat(foundItems.getFirst().getType()).isEqualTo(testItem.getType());
    assertThat(foundItems.getFirst().getBrand()).isEqualTo(testItem.getBrand());
  }

  @Test
  void findItemsCheaperThan_WhenPriceIsLessThanSpecified_ShouldReturnItemList() {
    double priceCap = 50.00;
    List<Item> foundItems = itemRepository.findItemsCheaperThan(priceCap);
    assertThat(foundItems).isNotEmpty();
    assertThat(foundItems.getFirst().getPrice()).isLessThan(priceCap);
  }

  @Test
  void findByType_WhenTypeExists_ShouldReturnItemList() {
    List<Item> foundItems = itemRepository.findByType(testItem.getType());
    assertThat(foundItems).isNotEmpty();
    assertThat(foundItems.getFirst().getType()).isEqualTo(testItem.getType());
  }
}
