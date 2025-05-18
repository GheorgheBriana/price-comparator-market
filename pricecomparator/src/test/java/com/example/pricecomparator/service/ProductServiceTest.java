package com.example.pricecomparator.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.example.pricecomparator.models.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceTest {

    private final FileService fileService = Mockito.mock(FileService.class);
    private final ProductService productService = new ProductService(fileService);

    @Test
    void testLoadProductsFromValidCsv() {
        String filePath = "csv/lidl_2025-05-01.csv";    
        List<Product> products = productService.loadProductsFromCsv(filePath);

        assertNotNull(products, "Products list should not be null");
        assertFalse(products.isEmpty(), "The list should not be empty");
    }

    @Test
    void testLoadProductsFromInvalidCsv() {
        String filePath = "csv/invalid_file.csv";
        List<Product> products = productService.loadProductsFromCsv(filePath);

        assertNotNull(products, "Product list should be created even if file is missing");
        assertTrue(products.isEmpty(), "The list should be empty if the file doesn't exist");
    }

    @Test
    void testLoadAllProductsFromCsvDirectory() {
        List<String> mockCsvFiles = List.of("csv/lidl_2025-05-01.csv");

        Mockito.when(fileService.getFileNames("csv", "", "")).thenReturn(mockCsvFiles);

        List<Product> allProducts = productService.loadAllProductsFromCsvDirectory();

        assertNotNull(allProducts, "List shouldn't be null");
        assertFalse(allProducts.isEmpty(), "Should load products from existing CSV file");
}

}
