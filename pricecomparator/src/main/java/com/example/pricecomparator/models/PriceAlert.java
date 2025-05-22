package com.example.pricecomparator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceAlert {
    private String productId;
    private double targetPrice;
    private String store;
}
