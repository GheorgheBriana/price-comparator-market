package com.example.pricecomparator.controller;

import java.util.List;

import com.example.pricecomparator.models.Discount;
import com.example.pricecomparator.dto.DiscountBestGlobalDTO;
import com.example.pricecomparator.dto.PriceHistoryDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.service.DiscountService;

@RestController
@RequestMapping("/discounts") // path
public class DiscountsController {

    private final DiscountService discountService;
    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);


    // Constructor
    public DiscountsController(DiscountService discountService) {
        this.discountService = discountService;
    }

    // GET /discounts/{store}/{date}
    @GetMapping("/{store}/{date}")
    public List<Discount> getAllDiscounts(
        @PathVariable String store,
        @PathVariable String date
    ) {
        String fileName = String.format("csv/%s_discounts_%s.csv", store, date);
        return discountService.loadDiscountFromCsv(fileName);
    }

    // GET /discounts/best-global
    // Returns a list of all products with the highest current percentage discounts across all stores,
    // filtered to include only active discounts (valid today), and sorted in descending order by discount value.
    @GetMapping("/best-global")
    public ResponseEntity<?> getGlobalBestDiscounts() {
        log.info("Received request for all global best discounts");

        try {
            // Load all active discounts (deduplicated per product, keeping the highest one)
            List<DiscountBestGlobalDTO> discounts = discountService.getGlobalTopDiscounts("csv");

            // If no discounts are found, return HTTP 204 No Content
            if (discounts.isEmpty()) {
                log.warn("No active discounts found.");
                return ResponseEntity.noContent().build();
            }

            // Return the list with HTTP 200 OK
            log.info("Returning {} active global discounts", discounts.size());
            return ResponseEntity.ok(discounts);

        } catch (IllegalStateException e) {
            // If CSV files are missing or something fails during processing, return HTTP 500
            log.error("Error loading discounts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // GET /discounts/new
    @GetMapping("/new")
    public ResponseEntity<List<Discount>> getNewDiscounts() {
        try {
            List<Discount> discounts = discountService.getNewDiscounts("csv");
            // if there are no new discounts => HTTP 204
            if(discounts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            // if there are new discounts => returns list + status 200
            return ResponseEntity.ok(discounts);
            // if there is other error => HTTP 500
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /discounts/price-history/{productId}?store={store}&brand={brand}&category={category}
    @GetMapping("/price-history/{productId}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistory(
        @PathVariable String productId,
        @RequestParam(required = false) String store,
        @RequestParam(required = false) String brand,
        @RequestParam(required = false) String category
    ) {
        List<PriceHistoryDTO> history = discountService.getPriceHistory(productId, store, brand, category);
        
        if(history.isEmpty()) {
            return ResponseEntity.noContent().build(); //HTTP 204
        }

        return ResponseEntity.ok(history); //HTTP 200 + list

    }
}
