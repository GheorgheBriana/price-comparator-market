// DTO used in the response for an optimised basket
// represents a specific product included in the shopping list of a store

package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketProductDTO {
    private String productId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private double totalPrice; // quantity * unitPrice
}
