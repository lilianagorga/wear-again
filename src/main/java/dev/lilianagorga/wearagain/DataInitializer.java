package dev.lilianagorga.wearagain;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.repository.ItemRepository;
import dev.lilianagorga.wearagain.repository.SaleRepository;
import dev.lilianagorga.wearagain.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import java.io.InputStreamReader;

@Component
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final ItemRepository itemRepository;
  private final SaleRepository saleRepository;
  private final PasswordEncoder passwordEncoder;


  public DataInitializer(UserRepository userRepository,
                         ItemRepository itemRepository,
                         SaleRepository saleRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.itemRepository = itemRepository;
    this.saleRepository = saleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    List<User> users = readUsersFromCsv("/users.csv");
    userRepository.saveAll(users);
    List<Item> items = readItemsFromCsv("/items.csv");
    itemRepository.saveAll(items);
    List<Sale> sales = readSalesFromCsv("/sales.csv");
    saleRepository.saveAll(sales);
  }


  private List<User> readUsersFromCsv(String csvFilePath) throws IOException {
    List<User> users = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(csvFilePath).getInputStream()))) {
      String line;
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] fields = line.split(";");
        User user = new User(
                fields[0],
                fields[1],
                fields[2],
                LocalDate.parse(fields[3], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fields[4],
                fields[5],
                fields[6],
                fields[7],
                passwordEncoder.encode(fields[8])
        );
        users.add(user);
      }
    }
    return users;
  }

  private List<Item> readItemsFromCsv(String csvFilePath) throws IOException {
    List<Item> items = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(csvFilePath).getInputStream()))) {
      String line;
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] fields = line.split(";");
        String cleanedPrice = fields[5].replace(" €", "").replace(",", ".");
        Item item = new Item(
                fields[0],
                LocalDate.parse(fields[1], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                fields[2],
                fields[3],
                fields[4],
                Double.parseDouble(cleanedPrice),
                fields[6].equalsIgnoreCase("SI")
        );
        items.add(item);
      }
    }
    return items;
  }

  private List<Sale> readSalesFromCsv(String csvFilePath) throws IOException {
    List<Sale> sales = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource(csvFilePath).getInputStream()))) {
      String line;
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] fields = line.split(";");
        Sale sale = new Sale(
                fields[0],
                fields[1],
                fields[2]
        );
        sales.add(sale);
      }
    }
    return sales;
  }
}




