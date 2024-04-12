package dev.lilianagorga.wearagain.controller;

import dev.lilianagorga.wearagain.model.Sale;
import dev.lilianagorga.wearagain.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class SaleControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SaleService saleService;

  private Sale sampleSale;

  @BeforeEach
  public void setUp() {
    sampleSale = new Sale("1", "itemId1", "userId1");
  }

  @Test
  public void createSale_ShouldReturnCreatedSale() throws Exception {
    when(saleService.createSale(any(Sale.class))).thenReturn(sampleSale);

    mockMvc.perform(post("/api/sales")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"itemId\":\"itemId1\",\"userId\":\"userId1\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(sampleSale.getUserId()));

    verify(saleService, times(1)).createSale(any(Sale.class));
  }

  @Test
  public void getAllSales_ShouldReturnAllSales() throws Exception {
    List<Sale> sales = Arrays.asList(sampleSale, new Sale("2", "itemId2", "userId2"));
    when(saleService.getAllSales()).thenReturn(sales);

    mockMvc.perform(get("/api/sales"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(sampleSale.getUserId()));

    verify(saleService, times(1)).getAllSales();
  }

  @Test
  public void getSaleById_WhenSaleExists_ShouldReturnSale() throws Exception {
    when(saleService.getSaleById("1")).thenReturn(Optional.of(sampleSale));

    mockMvc.perform(get("/api/sales/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(sampleSale.getId()));

    verify(saleService, times(1)).getSaleById("1");
  }

  @Test
  public void updateSale_WhenSaleExists_ShouldReturnUpdatedSale() throws Exception {
    Sale updatedSale = new Sale("1", "itemId2", "userId2");
    when(saleService.updateSale(eq("1"), any(Sale.class))).thenReturn(Optional.of(updatedSale));

    mockMvc.perform(put("/api/sales/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"itemId\":\"itemId2\",\"userId\":\"userId2\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.itemId").value(updatedSale.getItemId()));

    verify(saleService, times(1)).updateSale(eq("1"), any(Sale.class));
  }

  @Test
  public void deleteSale_WhenSaleExists_ShouldReturnSuccess() throws Exception {
    when(saleService.deleteSale("1")).thenReturn(Optional.of(sampleSale));

    mockMvc.perform(delete("/api/sales/1"))
            .andExpect(status().isOk());

    verify(saleService, times(1)).deleteSale("1");
  }

  @Test
  public void getSalesByUserId_WhenSalesExist_ShouldReturnSales() throws Exception {
    List<Sale> userSales = Arrays.asList(sampleSale, new Sale("2", "itemId2", "userId1"));
    when(saleService.getSalesByUserId("userId1")).thenReturn(userSales);

    mockMvc.perform(get("/api/sales/user/userId1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value("userId1"))
            .andExpect(jsonPath("$[1].itemId").value("itemId2"));

    verify(saleService, times(1)).getSalesByUserId("userId1");
  }

  @Test
  public void getSalesByUserId_WhenNoSalesExist_ShouldReturnNotFound() throws Exception {
    when(saleService.getSalesByUserId("userId1")).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/sales/user/userId1"))
            .andExpect(status().isNotFound());

    verify(saleService, times(1)).getSalesByUserId("userId1");
  }

  @Test
  public void getSalesByItemId_WhenSalesExist_ShouldReturnSales() throws Exception {
    List<Sale> itemSales = Arrays.asList(sampleSale, new Sale("3", "itemId1", "userId2"));
    when(saleService.getSalesByItemId("itemId1")).thenReturn(itemSales);

    mockMvc.perform(get("/api/sales/item/itemId1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].itemId").value("itemId1"))
            .andExpect(jsonPath("$[1].userId").value("userId2"));

    verify(saleService, times(1)).getSalesByItemId("itemId1");
  }

  @Test
  public void getSalesByItemId_WhenNoSalesExist_ShouldReturnNotFound() throws Exception {
    when(saleService.getSalesByItemId("itemId1")).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/sales/item/itemId1"))
            .andExpect(status().isNotFound());

    verify(saleService, times(1)).getSalesByItemId("itemId1");
  }
}

