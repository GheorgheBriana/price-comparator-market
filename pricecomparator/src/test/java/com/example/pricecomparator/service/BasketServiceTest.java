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

    @Test
    void testGetOptimisedBasket_withValidItems(){
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 2);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P003", 1);

        List<BasketRequestItemDTO> items = List.of(item1, item2);

        List<BasketResponseDTO> result = basketService.getOptimisedBasket(items);

        assertNotNull(result, "Basket result should not be null");
        assertFalse(result.isEmpty(), "Basket result should not be empty");

        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertTrue(total > 0, ":Total price should be greater than 0");
    }

    @Test
    void testGetOptimisedBasket_withInvalidItems(){
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("INVALID_ITEM1", 2);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("INVALID_ITEM2", 1);

        List<BasketRequestItemDTO> items = List.of(item1, item2);

        List<BasketResponseDTO> result = basketService.getOptimisedBasket(items);

        assertNotNull(result, "Basket result should not be null");
        assertTrue(result.isEmpty(), "Basket result should be empty for unknown product IDs");

        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertFalse(total > 0, ":Total price should 0 for unknown product IDs");
    }
}
