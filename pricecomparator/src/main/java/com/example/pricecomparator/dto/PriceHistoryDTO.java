package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDTO {
    private String productName;
    private String brand;
    private String category;
    private String from_date;
    private String store;
    private double percentageOfDiscount;
}

