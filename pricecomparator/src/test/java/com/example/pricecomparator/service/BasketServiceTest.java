package com.example.pricecomparator.service;

import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasketServiceTest {

    private final FileService fileService = new FileService();
    private final ProductService productService = new ProductService(fileService);
    private final BasketService basketService = new BasketService(productService);

    @Test
    void testOptimisedBasket_withValidProducts() {
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 2);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P003", 1);

        List<BasketRequestItemDTO> basketItems = List.of(item1, item2);

        List<BasketResponseDTO> result = basketService.getOptimisedBasket(basketItems);

        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Basket response should not be empty");

        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertTrue(total > 0, "Total price should be greater than 0");
    }

    @Test
    void testOptimisedBasket_withInvalidProducts() {
        BasketRequestItemDTO item = new BasketRequestItemDTO("UNKNOWN_ID", 1);
        List<BasketRequestItemDTO> basketItems = List.of(item);

        List<BasketResponseDTO> result = basketService.getOptimisedBasket(basketItems);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty when no matching products found");
    }
}
