package dev.lilianagorga.wearagain;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.repository.ItemRepository;
import dev.lilianagorga.wearagain.repository.SaleRepository;
import dev.lilianagorga.wearagain.repository.UserRepository;
import dev.lilianagorga.wearagain.service.ItemService;
import dev.lilianagorga.wearagain.service.SaleService;
import dev.lilianagorga.wearagain.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.util.ArrayUtils;

@Component
public class DataInitializer implements CommandLineRunner {

  @Value("${export.path}")
  private String exportPath;

  private final UserRepository userRepository;
  private final ItemRepository itemRepository;
  private final SaleRepository saleRepository;
  private final ItemService itemService;
  private final UserService userService;
  private final SaleService saleService;
  private final PasswordEncoder passwordEncoder;

  public DataInitializer(UserRepository userRepository,
                         ItemRepository itemRepository,
                         SaleRepository saleRepository,
                         ItemService itemService,
                         UserService userService,
                         SaleService saleService,
                         PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.itemRepository = itemRepository;
    this.saleRepository = saleRepository;
    this.itemService = itemService;
    this.userService = userService;
    this.saleService = saleService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(String... args) throws Exception {
    if (ArrayUtils.contains(args, "--cli")) {
      runCli();
    } else {
      List<User> users = readUsersFromCsv();
      userRepository.saveAll(users);
      List<Item> items = readItemsFromCsv();
      itemRepository.saveAll(items);
      List<Sale> sales = readSalesFromCsv();
      saleRepository.saveAll(sales);
    }
  }

  private void runCli() {
    Scanner scanner = new Scanner(System.in);
    boolean exit = false;
    while (!exit) {
      System.out.println("Enter command (0-6):");
      String commandInput = scanner.nextLine();
      int command = Integer.parseInt(commandInput);
      switch (command) {
        case 1:
          List<Item> allItems = itemService.getAllItems();
          allItems.forEach(item -> System.out.println(itemService.formatItemDetails(item)));
          break;
        case 2:
          System.out.println("Enter item ID:");
          String itemId = scanner.nextLine();
          System.out.println("Enter user ID:");
          String userId = scanner.nextLine();
          purchaseItem(itemId, userId);
          break;
        case 3:
          System.out.println("Enter sale ID:");
          String saleId = scanner.nextLine();
          returnItem(saleId);
          break;
        case 4:
          addUser(scanner);
          break;
        case 5:
          exportAvailableItems();
          break;
        case 6:
          System.out.println("Enter item ID to check availability:");
          String checkItemId = scanner.nextLine();
          checkItemAvailability(checkItemId);
          break;
        case 0:
          exit = true;
          break;
        default:
          System.out.println("Invalid command.");
      }
    }
    scanner.close();
  }

  private void purchaseItem(String itemId, String userId) {
    Optional<Item> item = itemService.getItemById(itemId);
    if (item.isPresent() && item.get().getAvailable()) {
      Sale sale = new Sale(null, itemId, userId);
      saleService.createSale(sale);
      item.get().setAvailable(false);
      itemService.updateItem(itemId, item.get());
      System.out.println("Item purchased successfully.");
    } else {
      System.out.println("Item not available for purchase.");
    }
  }

  private void returnItem(String saleId) {
    Optional<Sale> sale = saleService.getSaleById(saleId);
    if (sale.isPresent()) {
      Optional<Item> item = itemService.getItemById(sale.get().getItemId());
      if (item.isPresent()) {
        item.get().setAvailable(true);
        itemService.updateItem(item.get().getId(), item.get());
        saleService.deleteSale(saleId);
        System.out.println("Return item successfully.");
      } else {
        System.out.println("Item not found.");
      }
    } else {
      System.out.println("Sale not found.");
    }
  }


  private void addUser(Scanner scanner) {
    System.out.println("Add User Id:");
    String id = scanner.nextLine();
    if(userService.getUserById(id).isPresent()) {
      System.out.println("An user with this ID already exists.");
      return;
    }
    System.out.println("Add User Name:");
    String name = scanner.nextLine();
    System.out.println("Add User Surname:");
    String surname = scanner.nextLine();
    System.out.println("Add Birthdate (format dd/MM/yyyy):");
    String birthdateStr = scanner.nextLine();
    LocalDate birthdate = LocalDate.parse(birthdateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    System.out.println("Add Address:");
    String address = scanner.nextLine();
    System.out.println("Add DocumentId:");
    String documentId = scanner.nextLine();
    if(userService.getUserByDocumentId(documentId).isPresent()) {
      System.out.println("An user with this Document ID already exists.");
      return;
    }
    System.out.println("Add email:");
    String email = scanner.nextLine();
    if(userService.getUserByEmail(email).isPresent()) {
      System.out.println("An user with this email already exists.");
      return;
    }
    System.out.println("Add username:");
    String username = scanner.nextLine();
    if(userService.getUserByUsername(username).isPresent()) {
      System.out.println("An user with this username already exists.");
      return;
    }
    System.out.println("Add password:");
    String password = scanner.nextLine();

    User newUser = new User(id, name, surname, birthdate, address, documentId, email, username, passwordEncoder.encode(password));
    try {
      User savedUser = userService.createUser(newUser);
      System.out.println("User added successfully: " + savedUser);
    } catch (DataIntegrityViolationException e) {
      System.out.println("An error occurred during process: " + e.getMessage());
    }
  }


  private void exportAvailableItems() {
    List<Item> availableItems = itemService.findAvailableItems();
    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
    String fileName = exportPath + File.separator + "items_" + currentDate + ".csv";

    try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(Paths.get(fileName)))) {
      for (Item item : availableItems) {
            pw.println(item.getId() + ";" +
              item.getInsertDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ";" +
              item.getType() + ";" +
              item.getBrand() + ";" +
              item.getSize() + ";" +
              item.getPrice());
      }
      System.out.println("File exported: " + fileName);
    } catch (IOException e) {
      System.out.println("Error during exporting: " + e.getMessage());
    }
  }

  private void checkItemAvailability(String itemId) {
    Optional<Item> item = itemService.getItemById(itemId);
    if (item.isPresent()) {
      String availability = item.get().getAvailable() ? "available" : "not available";
      System.out.println("Item " + itemId + " is " + availability + ".");
    } else {
      System.out.println("Item with ID " + itemId + " not found.");
    }
  }


  private List<User> readUsersFromCsv() throws IOException {
    List<User> users = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("/users.csv").getInputStream()))) {
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

  private List<Item> readItemsFromCsv() throws IOException {
    List<Item> items = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("/items.csv").getInputStream()))) {
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

  private List<Sale> readSalesFromCsv() throws IOException {
    List<Sale> sales = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("/sales.csv").getInputStream()))) {
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



