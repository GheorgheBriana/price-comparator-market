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
    // Loads discount data for a specific store and date
    @GetMapping("/{store}/{date}")
    public List<Discount> getAllDiscounts(
        @PathVariable String store,
        @PathVariable String date
    ) {
        log.info("API call: GET /discounts/{}/{}", store, date);
        String fileName = String.format("csv/%s_discounts_%s.csv", store, date);
        return discountService.loadDiscountFromCsv(fileName);
    }

    // GET /discounts/best-global
    // Returns a list of all products with the highest current percentage discounts across all stores,
    // filtered to include only active discounts (valid today), and sorted in descending order by discount value.
    @GetMapping("/best-global")
    public ResponseEntity<?> getGlobalBestDiscounts() {
        log.info("API call: GET /discounts/best-global");

        try {
            // load all active discounts (deduplicated per product, keeping the highest one)
            List<DiscountBestGlobalDTO> discounts = discountService.getGlobalTopDiscounts("csv");

            // if no discounts are found, return HTTP 204 No Content
            if (discounts.isEmpty()) {
                log.warn("No active discounts found.");
                return ResponseEntity.noContent().build();
            }

            // if there are new discounts => return the list with HTTP 200 OK
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
    // Returns all discount entries from files uploaded in the last 24h
    @GetMapping("/new")
    public ResponseEntity<List<Discount>> getNewDiscounts() {
        log.info("API call: GET /discounts/new");

        try {
            List<Discount> discounts = discountService.getNewDiscounts("csv");
            
            // if there are no new discounts => HTTP 204
            if(discounts.isEmpty()) {
                log.warn("No new discounts found in the last 24h.");
                return ResponseEntity.noContent().build();
            }
            // if there are new discounts => returns list + status 200
            return ResponseEntity.ok(discounts);
            // if there is other error => HTTP 500
        } catch (Exception e) {
            log.error("Unexpected error while retrieving new discounts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /discounts/price-history?productId={}&store={}&brand={}&category={}&from={}&to={}
    // Endpoint for retrieving product price history (used for frontend graphs)
    // Supports optional filtering by productId, store, brand, category, and time range (from - to)
    @GetMapping("/price-history")
    public List<PriceHistoryDTO> getPriceHistory(
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String store,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String from,      // optional start date (inclusive)
            @RequestParam(required = false) String to         // optional end date (inclusive)
    ) {
        log.info("API call: GET /discounts/price-history?productId={}&store={}&brand={}&category={}&from={}&to={}",
                productId, store, brand, category, from, to);

        // call service method with all provided filters
        return discountService.getPriceHistory(productId, store, brand, category, from, to);
    }
}
