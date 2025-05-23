// DTO used to return the optimised basket per store
// represents a list of products grouped unde a specific store, with the total price

package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketResponseDTO {
    private String store;
    private List<BasketProductDTO> products; 
    private double totalPrice;
}
