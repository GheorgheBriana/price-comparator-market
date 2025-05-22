package com.example.pricecomparator.service.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.example.pricecomparator.controller.BasketController;
import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.BasketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(BasketController.class)
public class BasketControllerTest {

    // simulate HTTP requests
    @Autowired
    private MockMvc mockMvc;

    // inject Mock
    @MockBean
    private BasketService basketService;

    // transform Java objects in JSON
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testOptimiseBasket_WhenNoProductsFound_ReturnsRecommendation() throws Exception {
        // arrange, preparing data
        List<BasketRequestItemDTO> requestItems = List.of(
            new BasketRequestItemDTO("invalid-id",2)
        );

        // basketService.getOptimisedBasket(...) => should return empty List
        when(basketService.getOptimisedBasket(anyList())).thenReturn(Collections.emptyList());

        // act
        MvcResult result = mockMvc.perform(post("/basket/optimise") // simulates a POST request
            .contentType(MediaType.APPLICATION_JSON) // the type of request is JSON
            .content(objectMapper.writeValueAsString(requestItems))) // tranforms the list of Java objects in a JSON string
        .andExpect(status().isOk())
        .andReturn();

        // assert
        Map<String, Object> response = objectMapper.readValue(
            result.getResponse().getContentAsString(), 
            Map.class
        );

        assertEquals(200, result.getResponse().getStatus());
        assertTrue(response.containsKey("recommendation"));
        assertTrue(response.containsKey("baskets"));
        assertEquals("No products found for your cart.", response.get("recommendation"));
        assertEquals(0, ((List<?>) response.get("baskets")).size());    }
       
    @Test
    void optimiseBasket_whenProductsFound_returnsList() throws Exception {
        // arrange, preparing data
        List<BasketRequestItemDTO> requestItems = List.of(
            new BasketRequestItemDTO("Prod123", 2)
        );
        Product product = new Product(
            "Prod123",            // productId
            "Lapte",            // name
            "lactate",      // category
            "Napolact",               // brand
            1,              // quantity
            "l",                // unit
            10.0,                     // price
            "RON",                 // currency
            "Store1"                  // store
        );

        
        BasketResponseDTO basket = new BasketResponseDTO(
            "Store1",
            List.of(product, product),   // 2 bucăți ✕ 10 € = 20 €
            20.0
        );

        // basketService.getOptimisedBasket(...) => should return Basket
        when(basketService.getOptimisedBasket(anyList())).thenReturn(List.of(basket));

        // act
        MvcResult mvcResult = mockMvc.perform(
                post("/basket/optimise")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestItems))
        )
        .andExpect(status().isOk())
        .andReturn();

        // assert
        JsonNode root = objectMapper.readTree(
            mvcResult.getResponse().getContentAsString()
        );

        assertFalse(root.has("recommendation"));          
        assertEquals(1, root.get("baskets").size()); 
        JsonNode first = root.get("baskets").get(0);
        assertEquals("Store1", first.get("store").asText());
        assertEquals(20.0, first.get("totalPrice").asDouble());

    }
}