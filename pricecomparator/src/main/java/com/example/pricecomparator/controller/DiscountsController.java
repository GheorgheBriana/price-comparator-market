package com.example.pricecomparator.controller;

import java.util.List;

import com.example.pricecomparator.models.Discount;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.service.CSVService;

@RestController
@RequestMapping("/discounts") // path
public class DiscountsController {
    private final CSVService csvService;

    //Constructor
    public DiscountsController(CSVService csvService) {
        this.csvService = csvService;
    }

    // GET /discounts/{store}/{date}
    @GetMapping("/{store}/{date}")
    public List<Discount> getAllDiscounts(
        @PathVariable String store,
        @PathVariable String date
    ) {
        String fileName = String.format("csv/%s_discounts_%s.csv", store, date);
        return csvService.loadDiscounts(fileName);
    }
}
