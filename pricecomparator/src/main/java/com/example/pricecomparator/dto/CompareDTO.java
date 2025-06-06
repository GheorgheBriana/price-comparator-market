// DTO used for comparing a single product between two stores
// includes prices and indicates which store is cheaper
package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompareDTO {
    private String productId;
    private String productName;
    private double priceStore1;
    private double priceStore2;
    private String cheapestStore;
}
