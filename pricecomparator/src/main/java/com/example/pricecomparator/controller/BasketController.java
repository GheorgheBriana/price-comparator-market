package com.example.pricecomparator.controller;

import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import com.example.pricecomparator.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;
    private static final Logger log = LoggerFactory.getLogger(BasketController.class);

    // constructor to inject BasketService
    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    // POST /basket/optimise
    // accepts a basket with productIds + quantities, and returns the optimised list per store
    @PostMapping("/optimise")
    public ResponseEntity<Map<String, Object>> optimiseBasket(@RequestBody List<BasketRequestItemDTO> basketItems) {
        log.info("Received basket optimisation request with {} products", basketItems.size());

        // call the service to compute the best store combinations
        List<BasketResponseDTO> baskets = basketService.getOptimisedBasket(basketItems);

        // create a response map to include both the data and user-friendly message
        Map<String, Object> response = new LinkedHashMap<>(); // use LinkedHashMap to preserve insertion order, so "recommendation" field always appears first in JSON response

        // if not valid products were found => a warning message
        if (baskets.isEmpty()) {
            response.put("recommendation", "No products found in the system that match your basket.");
            response.put("baskets", baskets);
            return ResponseEntity.ok(response);
        }

        // if results were found => return a message and the optimised basket
        response.put("recommendation", "Your basket has been optimised across " + baskets.size() + " store(s). Check details below:");
        response.put("baskets", baskets);

        //HTTP 200 response + basket + message
        return ResponseEntity.ok(response);

    }
}
