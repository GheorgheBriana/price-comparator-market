// DTO used to represent product details along with value per unit
package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithValueDTO {
    private String name;
    private String brand;
    private String category;
    private String store;
    private double price;
    private double valuePerUnit; // calculated price per unit
    private String unit; // unit type
    private String note; // optional message
}

