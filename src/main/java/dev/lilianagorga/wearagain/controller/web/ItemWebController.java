package dev.lilianagorga.wearagain.controller.web;

import dev.lilianagorga.wearagain.model.Item;
import dev.lilianagorga.wearagain.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class ItemWebController {

  private final ItemService itemService;

  @Autowired
  public ItemWebController(ItemService itemService) {
    this.itemService = itemService;
  }

  @GetMapping("/items")
  public String viewItems(Model model) {
    List<Item> items = itemService.getAllItems();
    model.addAttribute("items", items);
    return "items";
  }

  @GetMapping("/items/{id}")
  public String viewItemDetails(@PathVariable String id, Model model) {
    Optional<Item> item = itemService.getItemById(id);
    model.addAttribute("item", item.orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id)));
    return "item-detail";
  }

}
