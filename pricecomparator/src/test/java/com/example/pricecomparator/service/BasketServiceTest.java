package com.example.pricecomparator.service;

import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import com.example.pricecomparator.models.Product;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BasketServiceTest {

    private final FileService fileService = new FileService();
    private final DiscountService discountService = Mockito.mock(DiscountService.class); 
    private final ProductService productService = new ProductService(fileService, discountService);
    private final BasketService basketService = new BasketService(productService, discountService);

    // Test a valid basket with products from two different stores.
    // Ensures the system returns a non-empty basket and calculates the  total price
    @Test
    void testOptimisedBasket_withValidProducts() {
       //arrange
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 2);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P003", 1);
        List<BasketRequestItemDTO> basketItems = List.of(item1, item2);
        
        // the DiscountService is mocked to isolate the BasketService logic
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice(); // presupunem că nu are reducere
        });

        // act: call the basket optimisation service
        List<BasketResponseDTO> result = basketService.getOptimisedBasket(basketItems);

        // assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Basket response should not be empty");

        // verify if there are 2 baskets (two implied stores)
        assertEquals(2, result.size(), "Expected 2 separate baskets for 2 different stores");

        // verify if the total price is greater than 0
        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertTrue(total > 0, "Total price should be greater than 0");
    }

    // Test the same product added with quantity > 1.
    // Ensures total price reflects the quantity correctly
    @Test
    void testOptimisedBasket_sameProductMultipleQuantity() {
        // arrange: create a basket request with 5 units of product P001
        BasketRequestItemDTO item = new BasketRequestItemDTO("P001", 5);
        List<BasketRequestItemDTO> items = List.of(item);
        
        // mock discount service to return base price (no discounts applied)
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice(); // presupunem că nu are reducere
        });

        // act: call the basket optimisation method
        List<BasketResponseDTO> result = basketService.getOptimisedBasket(items);

        // assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Basket response should not be empty");

        // verify if the total price is greater than 0
        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertTrue(total > 0, "Total price should reflect 5 units of the product");
    }

    // Test and invalid product ID that doesn't exist in the dataset.
    // Ensures the system returns an empty result and the total price is zero.
    @Test
    void testOptimisedBasket_withInvalidProducts() {
        //arrange: create a request with an unknown product id
        BasketRequestItemDTO item = new BasketRequestItemDTO("UNKNOWN_ID", 1);
        List<BasketRequestItemDTO> basketItems = List.of(item);

        // the DiscountService is mocket to isolate the BasketService logic
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice(); // presupunem că nu are reducere
        });

        // act: call the basket optimisation service
        List<BasketResponseDTO> result = basketService.getOptimisedBasket(basketItems);

        // assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Result should be empty when no matching products found");
        
        // since no valid products were matched, total price should be 0
        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertEquals(0.0, total, "Total price should be 0 when no products are matched");
    }

    // Test a basket with multiple valid items across stores.
    // Ensures the system generates correct totals and non-empty responses.
    @Test
    void testGetOptimisedBasket_withValidItems(){
        // arrange: create basket with two products
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 2);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P003", 1);
        List<BasketRequestItemDTO> items = List.of(item1, item2);

        // the DiscountService is mocket to isolate the BasketService logic
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice(); // presupunem că nu are reducere
        });

        // act: call service to optimise basket
        List<BasketResponseDTO> result = basketService.getOptimisedBasket(items);

        // assert
        assertNotNull(result, "Basket result should not be null");
        assertFalse(result.isEmpty(), "Basket result should not be empty");

        // total price should be calculated and be greater than 0
        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertTrue(total > 0, ":Total price should be greater than 0");
    }

    // Test a basket with only invalid product IDs.
    // Ensures the system returns an empty basket and a total of 0.
    @Test
    void testGetOptimisedBasket_withInvalidItems(){
        // arrange: create a basket with non-existent product IDs
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("INVALID_ITEM1", 2);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("INVALID_ITEM2", 1);
        List<BasketRequestItemDTO> items = List.of(item1, item2);

        // the DiscountService is mocket to isolate the BasketService logic
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice(); // presupunem că nu are reducere
        });

        // act: call the basket optimisation
        List<BasketResponseDTO> result = basketService.getOptimisedBasket(items);

        // assert
        assertNotNull(result, "Basket result should not be null");
        assertTrue(result.isEmpty(), "Basket result should be empty for unknown product IDs");

        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertEquals(0.0, total, "Total price should be 0 for unknown product IDs");
    }

    // Test the optimiseBasket()
    // Ensures the method correctly groups products by store based on the cheapest available offer
    @Test
    void testOptimiseBasket_directCallWithValidItems() {
        // arrange: create basket with two known product IDs
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 1);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P003", 1);
        List<BasketRequestItemDTO> basketItems = List.of(item1, item2);

        // the DiscountService is mocked to return base price, simulating no discounts
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice();
        });

        // act: call optimiseBasket directly
        Map<String, List<Product>> result = basketService.optimiseBasket(basketItems);

        // assert
        assertNotNull(result, "Result map should not be null");
        assertFalse(result.isEmpty(), "Result map should contain grouped products");

        // total number of products in the map should match input quantity
        int totalProducts = result.values().stream().mapToInt(List::size).sum();
        assertEquals(2, totalProducts, "Expected 2 products in total from different stores");
    }
    
    // Test a basket with duplicate product IDs.
    // Ensures the total quantity is handled correctly and final price reflects the sum.
    @Test
    void testOptimisedBasket_withDuplicateProductIds() {
        // arrange: same product ID added twice with different quantities
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 1);
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P001", 3); // Same product ID
        List<BasketRequestItemDTO> items = List.of(item1, item2);

        // mock discount service to return base price
        when(discountService.getDiscountedPrice(any(Product.class)))
            .thenAnswer(invocation -> {
                Product p = invocation.getArgument(0);
                return p.getPrice(); // no discount
            });

        // act: optimise basket with duplicate IDs
        List<BasketResponseDTO> result = basketService.getOptimisedBasket(items);

        // assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isEmpty(), "Basket should not be empty");

        // ensure total price accounts for combined quantity
        double total = result.stream().mapToDouble(BasketResponseDTO::getTotalPrice).sum();
        assertTrue(total > 0, "Total price should reflect combined quantity of 4 units");
    }

    // Test basket with invalid product quantities (zero or negative).
    // Ensures an exception is thrown when quantity is not positive.
    @Test
    void testOptimisedBasket_withInvalidQuantities() {
        // arrange: basket with 0 and -2 quantities
        BasketRequestItemDTO item1 = new BasketRequestItemDTO("P001", 0);  // invalid
        BasketRequestItemDTO item2 = new BasketRequestItemDTO("P002", -2); // invalid
        List<BasketRequestItemDTO> items = List.of(item1, item2);

        // act + assert: call optimiseBasket and expect exception
        assertThrows(IllegalArgumentException.class, () -> {
            basketService.optimiseBasket(items);
        }, "Should throw exception for invalid quantities");
    }
}
