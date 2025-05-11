package com.example.pricecomparator;

import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.CSVService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PricecomparatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricecomparatorApplication.class, args);

        // TEST: call CSVService to load products and print them
        CSVService csvService = new CSVService();
        List<Product> products = csvService.loadProducts("csv/lidl_2025-05-01");
        products.forEach(System.out::println);
    }
}
