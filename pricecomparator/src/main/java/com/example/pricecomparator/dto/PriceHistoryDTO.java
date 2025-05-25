// DTO used to show the price history of a product

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
    private String from_date; // the date the discount is valid for
    private String store;
    private double percentageOfDiscount; // the discount percentage
    private double basePrice; //the price before discount
    private double effectivePrice; // the final price after disocunt is applied
}

