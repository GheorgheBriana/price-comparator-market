package com.example.pricecomparator.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.pricecomparator.models.Product;

public class CSVServiceTest {
    private final CSVService csvService = new CSVService();

    @Test
    void testLoadProducts() {
        List<Product> products = csvService.loadProducts("csv/lidl_2025-05-01.csv");
        assertNotNull(products);
        assertFalse(products.isEmpty());
        assertTrue(products.size() > 5);
    }

}
