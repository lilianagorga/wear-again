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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.core.env.Environment;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class DataInitializerTest {

  @Autowired
  private DataInitializer dataInitializer;

  @Autowired
  private ApplicationContext context;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ItemRepository itemRepository;

  @MockBean
  private SaleRepository saleRepository;

  @MockBean
  private ItemService itemService;

  @MockBean
  private UserService userService;

  @MockBean
  private SaleService saleService;

//  @Test
//  public void testRunOnStartup() throws Exception {
//    String[] args = {};
//    dataInitializer.run(args);
//
//    verify(userRepository, times(2)).saveAll(any());
//    verify(itemRepository, times(2)).saveAll(any());
//    verify(saleRepository, times(2)).saveAll(any());
//  }


  @Test
  public void testRunCliOption1() throws Exception {
    String input = "1\n0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));
    List<Item> allItems = List.of(
            new Item("1", LocalDate.parse("2021-03-28"), "Sneakers", "Nike", "40", 60.00, true),
            new Item("3", LocalDate.parse("2021-07-06"), "Swimsuit", "Calzedonia", "2", 10.00, false)
    );
    when(itemService.getAllItems()).thenReturn(allItems);
    when(itemService.formatItemDetails(any(Item.class))).thenAnswer(invocation -> {
      Item item = invocation.getArgument(0);
      return String.format("ID: %s, Brand: %s", item.getId(), item.getBrand());
    });
    dataInitializer.run("--cli");

    verify(itemService, times(1)).getAllItems();

    System.setIn(System.in);
    System.setOut(System.out);

    String output = testOut.toString();
    System.out.println("Output captured: " + output);
    assertTrue(output.contains("Nike") && output.contains("Calzedonia"), "The output does not contain expected brands.");
  }

  @Test
  public void testRunCliOption2PurchaseItem() throws Exception {
    String itemId = "1";
    String userId = "1";
    String input = "2\n" + itemId + "\n" + userId + "\n0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    System.setIn(testIn);
    System.setOut(new PrintStream(testOut));

    Item mockItem = new Item(itemId, LocalDate.now(), "Sneakers", "Nike", "40", 60.00, true);
    Optional<Item> foundItem = Optional.of(mockItem);
    when(itemService.getItemById(itemId)).thenReturn(foundItem);

    Sale mockSale = new Sale(null, itemId, userId);
    when(saleService.createSale(any(Sale.class))).thenReturn(mockSale);
    when(itemService.updateItem(eq(itemId), any(Item.class))).thenReturn(null);

    String[] args = {"--cli"};
    dataInitializer.run(args);

    verify(itemService, times(1)).getItemById(itemId);
    verify(saleService, times(1)).createSale(any(Sale.class));
    verify(itemService, times(1)).updateItem(eq(itemId), any(Item.class));

    System.setIn(System.in);
    System.setOut(System.out);

    String output = testOut.toString();
    assertTrue(output.contains("Item purchased successfully."));
  }

  @Test
  public void testRunCliOption3ReturnItem() throws Exception {
    String saleId = "1";
    String itemId = "3";
    String userId = "1";
    String input = "3\n" + saleId + "\n0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    System.setIn(testIn);
    System.setOut(new PrintStream(testOut));

    Sale mockSale = new Sale(saleId, itemId, userId);
    Optional<Sale> foundSale = Optional.of(mockSale);
    when(saleService.getSaleById(saleId)).thenReturn(foundSale);

    Item mockItem = new Item(itemId, LocalDate.now(), "Swimsuit", "Calzedonia", "2", 10.00, false);
    when(itemService.getItemById(anyString())).thenReturn(Optional.of(mockItem));
    when(saleService.deleteSale(eq(saleId))).thenReturn(null);
    when(itemService.updateItem(eq(itemId), any(Item.class))).thenReturn(null);

    String[] args = {"--cli"};
    dataInitializer.run(args);

    verify(saleService, times(1)).getSaleById(saleId);
    verify(itemService, times(1)).getItemById(anyString());
    verify(saleService, times(1)).deleteSale(saleId);
    verify(itemService, times(1)).updateItem(anyString(), any(Item.class));

    System.setIn(System.in);
    System.setOut(System.out);

    String output = testOut.toString();
    assertTrue(output.contains("Return item successfully."));

    when(saleService.getSaleById("3")).thenReturn(Optional.empty());
    input = "3\n3\n0\n";
    testIn = new ByteArrayInputStream(input.getBytes());
    System.setIn(testIn);
    testOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(testOut));

    args = new String[]{"--cli"};
    dataInitializer.run(args);

    output = testOut.toString();
    assertTrue(output.contains("Sale not found."));
  }

  @Test
  public void testRunCliOption4AddUser() throws Exception {
    String input = "4\nUserId\nUserName\nUserSurname\n01/01/1990\nAddress\nDocumentId\nemail@example.com\nusername\npassword\n0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    System.setIn(testIn);
    System.setOut(new PrintStream(testOut));
    when(userService.getUserById("UserId")).thenReturn(Optional.empty());
    when(userService.getUserByDocumentId("DocumentId")).thenReturn(Optional.empty());
    when(userService.getUserByEmail("email@example.com")).thenReturn(Optional.empty());
    when(userService.getUserByUsername("username")).thenReturn(Optional.empty());
    when(userService.createUser(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      return new User("UserId", user.getName(), user.getSurname(), user.getBirthdate(),
              user.getAddress(), user.getDocumentId(), user.getEmail(),
              user.getUsername(), user.getPassword());
    });

    String[] args = {"--cli"};
    dataInitializer.run(args);

    verify(userService, times(1)).getUserById("UserId");
    verify(userService, times(1)).getUserByDocumentId("DocumentId");
    verify(userService, times(1)).getUserByEmail("email@example.com");
    verify(userService, times(1)).getUserByUsername("username");
    verify(userService, times(1)).createUser(any(User.class));

    System.setIn(System.in);
    System.setOut(System.out);
    String output = testOut.toString();
    assertTrue(output.contains("User added successfully"), "The output should confirm that a user was added.");
  }

  @Test
  public void testRunCliOption5ExportAvailableItems() throws Exception {
    String input = "5\n0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    System.setIn(testIn);
    System.setOut(new PrintStream(testOut));
    List<Item> availableItems = List.of(
            new Item("2", LocalDate.parse("2021-04-04"), "Sweater", "H&M", "M", 25.00, true),
            new Item("4", LocalDate.parse("2021-04-10"), "Jeans", "Levis", "XL", 20.00, true),
            new Item("5", LocalDate.parse("2021-11-15"), "T-shirt", "Denim", "S", 10.00, true)
    );
    when(itemService.findAvailableItems()).thenReturn(availableItems);
    String[] args = {"--cli"};
    dataInitializer.run(args);

    verify(itemService, times(1)).findAvailableItems();

    System.setIn(System.in);
    System.setOut(System.out);

    String output = testOut.toString();
    assertTrue(output.contains("File exported:"));
    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
    String fileName = "exported_csv" + File.separator + "items_" + currentDate + ".csv";
    File exportedFile = new File(fileName);
    assertTrue(exportedFile.exists(), "Exported file should exist.");

    List<String> fileLines = Files.readAllLines(Paths.get(fileName));
    assertTrue(fileLines.contains("2;04/04/2021;Sweater;H&M;M;25.0"), "File should contain item 2 details.");
    assertTrue(fileLines.contains("4;10/04/2021;Jeans;Levis;XL;20.0"), "File should contain item 4 details.");
    assertTrue(fileLines.contains("5;15/11/2021;T-shirt;Denim;S;10.0"), "File should contain item 5 details.");
  }

  @Test
  public void testRunCliOption6CheckItemAvailability() throws Exception {
    String itemId = "1";
    String input = "6\n" + itemId + "\n0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    System.setIn(testIn);
    System.setOut(new PrintStream(testOut));
    Item mockItem = new Item(itemId, LocalDate.now(), "Type", "Brand", "Size", 100.0, true);
    when(itemService.getItemById(itemId)).thenReturn(Optional.of(mockItem));

    String[] args = {"--cli"};
    dataInitializer.run(args);
    verify(itemService, times(1)).getItemById(itemId);

    System.setIn(System.in);
    System.setOut(System.out);

    String output = testOut.toString();
    assertTrue(output.contains("Item " + itemId + " is available."), "The output should confirm that the item is available.");
  }

  @Test
  public void testRunCliOption0Exit() throws Exception {
    String input = "0\n";
    ByteArrayInputStream testIn = new ByteArrayInputStream(input.getBytes());
    ByteArrayOutputStream testOut = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(testOut);
    System.setIn(testIn);
    System.setOut(ps);
    String[] args = {"--cli"};
    dataInitializer.run(args);

    ps.close();

    System.setIn(System.in);
    System.setOut(System.out);

    String output = testOut.toString();
    assertTrue(output.contains("Enter command"), "The CLI should prompt for command before exiting.");
  }

  @AfterEach
  public void tearDown() {
    String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy"));
    String fileName = "exported_csv" + File.separator + "items_" + currentDate + ".csv";
    File exportedFile = new File(fileName);
    if (exportedFile.exists()) {
      try {
        Files.delete(Paths.get(fileName));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}