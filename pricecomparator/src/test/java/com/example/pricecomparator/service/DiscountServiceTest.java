
package com.example.pricecomparator.service;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.pricecomparator.dto.DiscountBestGlobalDTO;
import com.example.pricecomparator.dto.PriceHistoryDTO;
import com.example.pricecomparator.models.Discount;

import static org.junit.jupiter.api.Assertions.*;

public class DiscountServiceTest {

    private final FileService fileService = new FileService();
    private final DiscountService discountService = new DiscountService(fileService);

    // Test loading discounts from valid CSV file
    @Test
    void testLoadDiscountsFromValidCsv() {
        List<Discount> discounts = discountService.loadDiscountFromCsv("csv/lidl_discounts_2025-05-01.csv");
        assertNotNull(discounts);
        assertFalse(discounts.isEmpty());
    }

    // Test loading discounts from invalid file path
    @Test
    void testLoadDiscountsFromInvalidCsv() {
        List<Discount> discounts = discountService.loadDiscountFromCsv("csv/nonexistent_file.csv");
        assertNotNull(discounts);
        assertTrue(discounts.isEmpty());
    }

    // Test fetching best discounts
    @Test
    void testGetBestDiscounts() {
        List<Discount> discounts = discountService.getBestDiscounts("csv", "lidl", "2025-05-01");
        assertNotNull(discounts);
        assertFalse(discounts.isEmpty());
        for (int i = 0; i < discounts.size() - 1; i++) {
            assertTrue(discounts.get(i).getPercentageOfDiscount() >= discounts.get(i + 1).getPercentageOfDiscount());
        }
    }

    // Test error on invalid date format
    @Test
    void testGetBestDiscountsInvalidDate() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            discountService.getBestDiscounts("csv", "lidl", "May-01-2025");
        });
        assertTrue(ex.getMessage().contains("Invalid date"));
    }

    // Test global top discounts
    @Test
    void testGetGlobalTopDiscounts() {
        List<DiscountBestGlobalDTO> top = discountService.getGlobalTopDiscounts("csv");
        assertNotNull(top);
        assertFalse(top.isEmpty());
        for (int i = 0; i < top.size() - 1; i++) {
            assertTrue(top.get(i).getPercentageOfDiscount() >= top.get(i + 1).getPercentageOfDiscount());
        }
    }

    // Test retrieving newly added discounts in the last 24 hours
    @Test
    void testGetNewDiscounts() {
        List<Discount> discounts = discountService.getNewDiscounts("csv");
        assertNotNull(discounts);
        assertFalse(discounts.isEmpty());
    }

    // Test price history without date range
    @Test
    void testGetPriceHistoryWithFilters() {
        List<PriceHistoryDTO> history = discountService.getPriceHistory("P001", "lidl", "Zuzu", "lactate", null, null);
        assertNotNull(history);
        assertFalse(history.isEmpty());
        for (PriceHistoryDTO dto : history) {
            assertEquals("lidl", dto.getStore().toLowerCase());
        }
    }

    // Test price history with full date range filtering
    @Test
    void testGetPriceHistoryWithDateRange() {
        List<PriceHistoryDTO> history = discountService.getPriceHistory("P001", "lidl", "Zuzu", "lactate", "2025-05-01", "2025-05-03");
        assertNotNull(history);
        assertFalse(history.isEmpty());
        for (PriceHistoryDTO dto : history) {
            assertTrue(dto.getFrom_date().compareTo("2025-05-01") >= 0);
            assertTrue(dto.getFrom_date().compareTo("2025-05-03") <= 0);
        }
    }

    // Test price history without filters (only by productId)
    @Test
    void testGetPriceHistoryWithoutFilters() {
        List<PriceHistoryDTO> history = discountService.getPriceHistory("P001", null, null, null, null, null);
        assertNotNull(history);
        assertFalse(history.isEmpty());
    }
}
