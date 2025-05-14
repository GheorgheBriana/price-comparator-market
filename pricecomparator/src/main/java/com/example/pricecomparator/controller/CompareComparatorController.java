package com.example.pricecomparator.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.dto.CompareDTO;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.CSVService;
import com.example.pricecomparator.service.CompareService;

@RestController
@RequestMapping("/compare") //path
public class CompareComparatorController {

    private final CSVService csvService;
    private final CompareService comparatorService;

    public CompareComparatorController() {
        this.csvService = new CSVService();
        this.comparatorService = new CompareService();
    }
    
    @GetMapping("/{store1}/{date1}/{store2}/{date2}")
    public List<CompareDTO> compareProducts(
        @PathVariable String store1,
        @PathVariable String date1,
        @PathVariable String store2,
        @PathVariable String date2
    ) {
        // build filenames for both stores
        String file1 = String.format("csv/%s_%s.csv", store1, date1);
        String file2 = String.format("csv/%s_%s.csv", store2, date2);

        // load products from both files
        List<Product> productsStore1 = csvService.loadProducts(file1);
        List<Product> productsStore2 = csvService.loadProducts(file2);
        
        //compare and return result
        return comparatorService.compareProducts(productsStore1, productsStore2);
    }
}
