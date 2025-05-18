package com.example.pricecomparator.controller;

import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import com.example.pricecomparator.service.BasketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping("/optimise")
    public ResponseEntity<Map<String, Object>> optimiseBasket(@RequestBody List<BasketRequestItemDTO> basketItems) {
        List<BasketResponseDTO> baskets = basketService.getOptimisedBasket(basketItems);

        Map<String, Object> response = new HashMap<>();

        if (baskets.isEmpty()) {
            response.put("recommendation", "No products found for your cart.");
            response.put("baskets", baskets);
            return ResponseEntity.ok(response);
        }
        response.put("baskets", baskets);

        return ResponseEntity.ok(response);
    }
}
