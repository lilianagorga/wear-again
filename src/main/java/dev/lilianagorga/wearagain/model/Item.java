package dev.lilianagorga.wearagain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "items")
public class Item {
  @Id
  private String id;
  private LocalDate insertDate;
  @Indexed
  private String type;
  private String brand;
  private String size;
  private Double price;
  private Boolean available;
}
