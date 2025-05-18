package com.example.pricecomparator.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.pricecomparator.models.Product;

public class ProductServiceTest {
    private final ProductService productService = new ProductService();

    @Test
    void testLoadProductsFromValidCsv() {
        String filePath = "csv/lidl_2025-05-01.csv";    
        List<Product> products = productService.loadProductsFromCsv(filePath);

        // result should not be null
        assertNotNull(products, "Products list should not be null");

        // verify if list is empty
        assertFalse(products.isEmpty(), "The list should not be null");
    }

    @Test
    void testLoadProductsFromInvalidCsv() {
        String filePath = "csv/invalid_file.csv";
        List<Product> products = productService.loadProductsFromCsv(filePath);

        assertNotNull(products, "Product list should be created even if file is missing");

        assertTrue(products.isEmpty(), "The list should be empty, if the file doesn't exist");
    }

}