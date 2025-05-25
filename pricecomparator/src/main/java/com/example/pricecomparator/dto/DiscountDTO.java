// DTO representing a discount available for a product
package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDTO {
    private String productName;
    private String brand;
    private double percentageOfDiscount;
    private String storeName;
}
