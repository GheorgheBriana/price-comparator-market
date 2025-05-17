package com.example.pricecomparator.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.dto.CompareDTO;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.ProductService;
import com.example.pricecomparator.service.CompareService;
import com.example.pricecomparator.service.FileService;

@RestController
@RequestMapping("/compare") //path
public class CompareController {
    private final ProductService productService;
    private final CompareService comparatorService;
    private final FileService fileService;

    public CompareController(ProductService productService, CompareService compareService, FileService fileService) {
        this.productService = productService;
        this.comparatorService = compareService;
        this.fileService = fileService;
    }
    
    @GetMapping("/{store1}/{date1}/{store2}/{date2}")
    public List<CompareDTO> compareProducts(
        @PathVariable String store1,
        @PathVariable String date1,
        @PathVariable String store2,
        @PathVariable String date2
    ) {
        // build filenames for both stores
        List<String> store1Files = fileService.getFileNames("csv", store1, date1);
        List<String> store2Files = fileService.getFileNames("csv", store2, date2);

        if (store1Files.isEmpty() || store2Files.isEmpty()) {
            throw new RuntimeException(
                "No CSV found for " + store1 + "/" + date1 + " or " + store2 + "/" + date2);
        }

        String file1 = store1Files.get(0);
        String file2 = store2Files.get(0);

        // load products from both files using ProductService
        List<Product> productsStore1 = productService.loadProductsFromCsv(file1);
        List<Product> productsStore2 = productService.loadProductsFromCsv(file2);

        System.out.println("Loaded " + productsStore1.size() + " products from " + file1);
        System.out.println("Loaded " + productsStore2.size() + " products from " + file2);

        // compare
        return comparatorService.compareProducts(productsStore1, productsStore2);
    }
}
