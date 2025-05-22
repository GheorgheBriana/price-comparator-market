package com.example.pricecomparator.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.dto.BestValueRecommendationDTO;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/products") // path for products API
public class ProductsController {
    private static final Logger log = LoggerFactory.getLogger(ProductsController.class);

    @Autowired
    private final ProductService productService;

    // constructor
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    // GET /products/{store}/{date}
    @GetMapping("/{store}/{date}")
    public List<Product> getAllProducts(
            @PathVariable String store,
            @PathVariable String date) {
                
        // build file based on store and date    
        String fileName = String.format("csv/%s_%s.csv", store, date);
        log.info("Requested all products from file: {}", fileName);

        // load products from CSV
        return productService.loadProductsFromCsv(fileName);
    }

    //GET /products/best-value
    @GetMapping("/best-value")
    public ResponseEntity<BestValueRecommendationDTO> getBestValueProducts(
            @RequestParam String category,
            @RequestParam(defaultValue = "5") int top) {
        
        log.info("Received request for best value products in category '{}' (top {})", category, top);
        
        // get top products sorted by lowest price per base unit
        List<Product> bestValueProducts = productService.getBestValueProductsByCategory(category, top);

        // if not products were found => return 204
        if (bestValueProducts.isEmpty()) {
            log.warn("No products found for category '{}'", category);
            return ResponseEntity.noContent().build();
        }

        // get the first product in the list (the most cost-effective)
        Product bestProduct = bestValueProducts.get(0);

        // build recommendation message with product details
        String message = String.format(
            "The product '%s' from store '%s' is the most cost-effective in the '%s' category (%.2f RON/%s).",
            bestProduct.getProductName(),
            bestProduct.getStore(),
            category,
            bestProduct.getPricePerBaseUnit(),
            bestProduct.getPackageUnit()
        );

        // log which product was selected as the best value
        log.info("Best value product: {} from store {} ({} RON/{})",
            bestProduct.getProductName(),
            bestProduct.getStore(),
            bestProduct.getPricePerBaseUnit(),
            bestProduct.getPackageUnit()
        );

        // create response object with message + product list
        BestValueRecommendationDTO response = new BestValueRecommendationDTO(message, bestValueProducts);
        
        return ResponseEntity.ok(response);
    }

}

