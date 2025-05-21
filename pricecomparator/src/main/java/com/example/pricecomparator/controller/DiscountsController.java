package com.example.pricecomparator.controller;

import java.util.List;

import com.example.pricecomparator.models.Discount;
import com.example.pricecomparator.dto.DiscountBestGlobalDTO;
import com.example.pricecomparator.dto.PriceHistoryDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.service.DiscountService;

@RestController
@RequestMapping("/discounts") // path
public class DiscountsController {

    private final DiscountService discountService;

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
    @GetMapping("/best-global")
    public ResponseEntity<?> getGlobalBestDiscounts() {
        try {
            List<DiscountBestGlobalDTO> discounts = discountService.getGlobalTopDiscounts("csv");
            return ResponseEntity.ok(discounts);
        } catch (IllegalStateException e) {
            return ResponseEntity.notFound().build();
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

    // GET /discounts/price-history/{productId}
    @GetMapping("/price-history/{productId}")
    public ResponseEntity<List<PriceHistoryDTO>> getPriceHistory(
        @PathVariable String productId
    ) {
        List<PriceHistoryDTO> history = discountService.getPriceHistory(productId);
        
        if(history.isEmpty()) {
            return ResponseEntity.noContent().build(); //HTTP 204
        }

        return ResponseEntity.ok(history); //HTTP 200 + list

    }
}
