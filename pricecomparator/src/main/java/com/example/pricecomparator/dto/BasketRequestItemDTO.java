package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketRequestItemDTO {
    public String productId;
    private int quantity;
}
