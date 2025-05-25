package com.example.pricecomparator.controller;

import java.util.List;

import com.example.pricecomparator.dto.BestValueRecommendationDTO;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.ProductService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products") // base path for all product-related endpoints
public class ProductsController {
    private static final Logger log = LoggerFactory.getLogger(ProductsController.class);

    @Autowired
    private final ProductService productService;

    // Constructor for injecting ProductService
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    // GET /products/{store}/{date}
    // Loads product data for a specific store and date from a corresponding CSV file.
    @GetMapping("/{store}/{date}")
    public List<Product> getAllProducts(
            @PathVariable String store,
            @PathVariable String date) {

        log.info("API call: GET /products/{}/{}", store, date);

        // Construct file path from store and date
        String fileName = String.format("csv/%s_%s.csv", store, date);
        log.debug("Loading products from file: {}", fileName);

        // Load products from the specified CSV file
        return productService.loadProductsFromCsv(fileName);
    }

    // GET /products/best-value?category=&top=5
    // Returns a list of top N products with the best value per unit
    // for a specific category, sorted in ascending order of unit price.
    @GetMapping("/best-value")
    public ResponseEntity<BestValueRecommendationDTO> getBestValueProducts(
            @RequestParam String category,
            @RequestParam(defaultValue = "5") int top) {

        log.info("API call: GET /products/best-value?category={}&top={}", category, top);

        // Retrieve the top N products by lowest price per base unit
        List<Product> bestValueProducts = productService.getBestValueProductsByCategory(category, top);

        // If no products found for the given category, return HTTP 204 No Content
        if (bestValueProducts.isEmpty()) {
            log.warn("No products found for category '{}'", category);
            return ResponseEntity.noContent().build();
        }

        // Pick the best product (first in sorted list)
        Product bestProduct = bestValueProducts.get(0);

        // Generate a recommendation message
        String message = String.format(
                "The product '%s' from store '%s' is the most cost-effective in the '%s' category (%.2f RON/%s).",
                bestProduct.getProductName(),
                bestProduct.getStore(),
                category,
                bestProduct.getPricePerBaseUnit(),
                bestProduct.getPackageUnit()
        );

        log.info("Best value recommendation: {} ({:.2f RON/{}})",
                bestProduct.getProductName(),
                bestProduct.getPricePerBaseUnit(),
                bestProduct.getPackageUnit());

        // Build and return response DTO
        BestValueRecommendationDTO response = new BestValueRecommendationDTO(message, bestValueProducts);
        return ResponseEntity.ok(response);
    }
    // GET /products/{productId}/substitutes?top=&sameBrand=
    // Returns a list of top N substitute products for a given productId.
    // Substitute products are from the same category (and optionally same brand),
    // and are sorted by best value per unit (after discount).
    @GetMapping("/{productId}/substitutes")
    public ResponseEntity<?> getProductSubstitutes(
            @PathVariable String productId,
            @RequestParam(defaultValue = "3") int top,
            @RequestParam(defaultValue = "false") boolean sameBrand
    ) {
        log.info("API call: GET /products/{}/substitutes?top={}&sameBrand={}", productId, top, sameBrand);

        try {
            // Call service to retrieve substitute products
            var substitutes = productService.getProductSubstitutes(productId, top, sameBrand);

            // If no substitutes found => HTTP 204
            if (substitutes.isEmpty()) {
                log.warn("No substitute products found for ID '{}'", productId);
                return ResponseEntity.noContent().build();
            }

            log.info("Returning {} substitute(s) for product ID '{}'", substitutes.size(), productId);
            return ResponseEntity.ok(substitutes);

        } catch (RuntimeException e) {
            // If original product is not found or any error occurs => HTTP 404
            log.error("Error finding substitutes for product ID '{}': {}", productId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }

}
