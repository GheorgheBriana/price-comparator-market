package com.example.pricecomparator.controller;

import java.util.List;

import com.example.pricecomparator.models.Discount;
import com.example.pricecomparator.dto.DiscountBestGlobalDTO;

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
}
