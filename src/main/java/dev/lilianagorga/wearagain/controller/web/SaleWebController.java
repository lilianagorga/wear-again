package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.model.User;
import dev.lilianagorga.wearagain.service.SaleService;
import dev.lilianagorga.wearagain.service.ItemService;
import dev.lilianagorga.wearagain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sales")
public class SaleWebController {

  private final SaleService saleService;
  private final ItemService itemService;
  private final UserService userService;

  @Autowired
  public SaleWebController(SaleService saleService, ItemService itemService, UserService userService) {
    this.saleService = saleService;
    this.itemService = itemService;
    this.userService = userService;
  }

  @GetMapping
  public String getAllSales(Model model) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = null;
    boolean showModal = false;

    if (authentication.getPrincipal() instanceof UserDetails) {
      username = ((UserDetails) authentication.getPrincipal()).getUsername();
    } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
      username = ((DefaultOidcUser) authentication.getPrincipal()).getEmail();
      showModal = true;
    }

    if (username != null) {
      User user = userService.getUserByUsername(username)
              .orElse(null);

      if (user != null) {
        String userId = user.getId();
        List<Sale> sales = saleService.getSalesByUserId(userId);
        if (sales == null) {
          sales = new ArrayList<>();
        }
        List<Item> items = sales.stream()
                .map(sale -> itemService.getItemById(sale.getItemId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with id: " + sale.getItemId())))
                .collect(Collectors.toList());
        List<User> users = sales.stream()
                .map(sale -> userService.getUserById(sale.getUserId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + sale.getUserId())))
                .collect(Collectors.toList());

        model.addAttribute("sales", sales);
        model.addAttribute("items", items);
        model.addAttribute("users", users);
      } else {
        showModal = true;
        model.addAttribute("sales", new ArrayList<>());
      }
    } else {
      return "redirect:/login";
    }
    model.addAttribute("showModal", showModal);
    return "sales-history";
  }

  @GetMapping("/{id}")
  public String getSaleDetails(@PathVariable String id, Model model) {
    Sale sale = saleService.getSaleById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid sale Id:" + id));
    Item item = itemService.getItemById(sale.getItemId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + sale.getItemId()));
    User user = userService.getUserById(sale.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + sale.getUserId()));

    model.addAttribute("sale", sale);
    model.addAttribute("item", item);
    model.addAttribute("user", user);
    return "sale-detail";
  }

}

