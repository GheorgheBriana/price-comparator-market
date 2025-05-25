package com.example.pricecomparator.service;

import org.junit.jupiter.api.Test;

import com.example.pricecomparator.dto.ProductWithValueDTO;
import com.example.pricecomparator.models.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private final FileService fileService = new FileService();
    private final DiscountService discountService = new DiscountService(fileService);
    private final ProductService productService = new ProductService(fileService, discountService);


    // Test getBestValueProductsByCategory with known category
    // Should return a sorted list of top products by price per unit
    @Test
    void testGetBestValueProducts_validCategory_returnsSortedList() {
        List<Product> products = productService.getBestValueProductsByCategory("lactate", 3);

        assertNotNull(products);
        assertFalse(products.isEmpty());

        for (int i = 1; i < products.size(); i++) {
            double prev = products.get(i - 1).getPricePerBaseUnit();
            double curr = products.get(i).getPricePerBaseUnit();
            assertTrue(prev <= curr, "List should be sorted by value per unit");
        }
    }

    // Test getBestValueProductsByCategory with unknown category
    // Should return an empty list
    @Test
    void testGetBestValueProducts_invalidCategory_returnsEmpty() {
        List<Product> products = productService.getBestValueProductsByCategory("nonexistent", 3);

        assertNotNull(products);
        assertTrue(products.isEmpty());
    }

    // Test getProductSubstitutes for a valid productId and sameBrand=false
    // Should return substitutes from same category (any brand)
    @Test
    void testGetProductSubstitutes_validId_noBrandFilter_returnsSubstitutes() {
        List<ProductWithValueDTO> substitutes = productService.getProductSubstitutes("P001", 3, false);

        assertNotNull(substitutes);
        assertFalse(substitutes.isEmpty());

        String expectedCategory = substitutes.get(0).getCategory();
        for (ProductWithValueDTO dto : substitutes) {
            assertEquals(expectedCategory.toLowerCase(), dto.getCategory().toLowerCase());
        }
    }

    // Test getProductSubstitutes for a valid productId and sameBrand=true
    // Should return substitutes from same category and same brand
    @Test
    void testGetProductSubstitutes_validId_sameBrandOnly_returnsFilteredSubstitutes() {
        String productId = "P004"; // ID-ul pe care îl testezi

        // Call method
        List<ProductWithValueDTO> substitutes = productService.getProductSubstitutes(productId, 1, true);

        // Verify list is returned
        assertNotNull(substitutes);
        assertFalse(substitutes.isEmpty());

        // Get original product brand
        Product original = productService.loadAllProductsFromCsvDirectory().stream()
            .filter(p -> p.getProductId().equalsIgnoreCase(productId)) // <-- corect
            .findFirst()
            .orElseThrow();

        String expectedBrand = original.getBrand();

        // Verify all returned substitutes have same brand
        for (ProductWithValueDTO dto : substitutes) {
            assertEquals(expectedBrand.toLowerCase(), dto.getBrand().toLowerCase());
        }
    }


    // Test getProductSubstitutes with unknown productId
    // Should throw RuntimeException
    @Test
    void testGetProductSubstitutes_invalidId_throwsException() {
        Exception ex = assertThrows(RuntimeException.class, () -> {
            productService.getProductSubstitutes("INVALID_ID", 3, false);
        });

        assertTrue(ex.getMessage().contains("Product not found"));
    }
}
