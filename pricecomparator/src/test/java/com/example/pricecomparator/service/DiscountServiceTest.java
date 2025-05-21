package com.example.pricecomparator.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.pricecomparator.dto.DiscountBestGlobalDTO;
import com.example.pricecomparator.dto.PriceHistoryDTO;
import com.example.pricecomparator.models.Discount;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountServiceTest {
    private final FileService fileService = new FileService();
    private final DiscountService discountService = new DiscountService(fileService);

    //---------------------------------
    // Test LoadDiscountFromCsv method
    //---------------------------------
    @Test
    void testLoadDiscountsFromValidCsv() {
        String filePath = "csv/lidl_discounts_2025-05-01.csv";
        List<Discount> discounts = discountService.loadDiscountFromCsv(filePath);

        // verify if result in not null
        assertNotNull(discounts, "Discount list should not be null");

        // verify is discount list is empty
        assertFalse(discounts.isEmpty(), "Discount list should not be empty");
    }

    @Test
    void testLoadDiscountsFromInvalidCsv() {
        String filePath = "csv/invalid_file.csv";
        List<Discount> discounts = discountService.loadDiscountFromCsv(filePath);

        assertNotNull(discounts, "Discount list should not be null even if file is missing");

        assertTrue(discounts.isEmpty(),"Discount list should be empty if file does not exist");
    }

    //--------------------------------
    // Test GetBestDiscounts method
    //--------------------------------
    @Test
    void testGetBestDiscountsValidInput() {
        String directoryPath = "csv";
        String store = "lidl";
        String date = "2025-05-01";

        List<Discount> result = discountService.getBestDiscounts(directoryPath, store, date);

        // verify if result is not null
        assertNotNull(result, "Discount list should not be null");

        // verify if result returns at least one discount
        assertFalse(result.isEmpty(), "Should return at least one discount");

        // verify if list is sorted descending by discount percentage
        for(int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                result.get(i).getPercentageOfDiscount() >= result.get(i+1).getPercentageOfDiscount(), "Discounts should be sorted descending by percentage"
            );
        }
    }

    @Test
    void testGetBestDiscountsWithInvalidDate() {
        String directoryPath = "csv";
        String store = "lidl";
        String invalidDate = "may-2025";

        // verify if the methods throws an IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            discountService.getBestDiscounts(directoryPath, store, invalidDate);
        });

        String expectedMessage = "Invalid date. Use the YYYY-MM-DD format";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Error message should mention date format");       
    }
    
    //--------------------------------
    // Test GetGlobalTopDiscounts method
    //--------------------------------
    @Test
    void testGetGlobalTopDiscounts() {
        String directoryPath = "csv";

        List<DiscountBestGlobalDTO> result = discountService.getGlobalTopDiscounts(directoryPath);

        // verify if result is not null
        assertNotNull(result, "Discount list should not be null");

        // verify if list has at least one discount
        assertFalse(result.isEmpty(), "There should be at least one discount in global list");

        // verify if list is sorted descending by discount percentage
        for(int i = 0; i < result.size() - 1; i++) {
            assertTrue(
                result.get(i).getPercentageOfDiscount() >= result.get(i+1).getPercentageOfDiscount(), "Discounts should be sorted descending by percentage"
            );
        }
    }

    //--------------------------------
    // Test GetNewDiscounts method
    //--------------------------------
    @Test
    void testGetNewDiscountsReturnsRecentDiscounts() {
        String directoryPath = "csv";

        // call method that should return discounts added in the last 24h
        List<Discount> newDiscounts = discountService.getNewDiscounts(directoryPath);

        // verify if result is not null
        assertNotNull(newDiscounts, "List of new discounts should not be null");

        // verify if list contains at least one discount (based on test CSVs)
        assertFalse(newDiscounts.isEmpty(), "There should be at least one recent discount");

        // define yesterday's date to compare against
        LocalDate yesterday = LocalDate.now().minusDays(1);
        ZoneId zoneId = ZoneId.systemDefault();

        // check that each discount has a valid fromDate and it is not older than yesterday
        for (Discount discount : newDiscounts) {
            assertNotNull(discount.getFromDate(), "fromDate should not be null");

            LocalDate fromDate = discount.getFromDate().toInstant().atZone(zoneId).toLocalDate();

            // verify if the discount was added yesterday or today
            assertFalse(fromDate.isBefore(yesterday), "Discount should be from yesterday or today");
        }
    }

    //--------------------------------
    // Test GetPriceHistory method
    //--------------------------------
    @Test
    void testGetPriceHistoryWithFilters() {
        String productId = "P001";
        String store = "lidl";
        String brand = "Zuzu";
        String category = "lactate";

        // call method that returns price history for given filters
        List<PriceHistoryDTO> history = discountService.getPriceHistory(productId, store, brand, category);

        // verify is result is null
        assertNotNull(history, "Price history should not be null");

        // verify if result contains at least one record
        assertFalse(history.isEmpty(), "There should be at least one entry for this product");

        // verify if each result matches the store filter
        for(PriceHistoryDTO dto : history) {
            assertEquals(store.toLowerCase(), dto.getStore().toLowerCase(), "Store should match filter");
        }
    }

    @Test
    void testGetPriceHistory_WithoutFilters() {
        String productId = "P001";

        // call method without filters
        List<PriceHistoryDTO> history = discountService.getPriceHistory(productId, null, null, null);

        // verify result is not null
        assertNotNull(history, "Price history should not be null");

        // verify result contains at least one entry
        assertFalse(history.isEmpty(), "Should return at least one entry for the given product ID");

        // print the result (optional, util dacă vrei să vezi ce returnează)
        for (PriceHistoryDTO dto : history) {
            System.out.println(dto);
        }
    }

}
