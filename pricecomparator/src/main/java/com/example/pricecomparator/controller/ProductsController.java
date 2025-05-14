package com.example.pricecomparator.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.CSVService;

@RestController
@RequestMapping("/products") // path for products API
public class ProductsController {
    private final CSVService csvService;

    // constructor
    public ProductsController(CSVService csvService) {
        this.csvService = csvService;
    }

    // GET
    @GetMapping("/{store}/{date}")
    public List<Product> getAllProducts(
            @PathVariable String store,
            @PathVariable String date) {
                
        // build file based on store and date    
        String fileName = String.format("csv/%s_%s.csv", store, date);

        // load products from CSV
        return csvService.loadProducts(fileName);
    }
}

