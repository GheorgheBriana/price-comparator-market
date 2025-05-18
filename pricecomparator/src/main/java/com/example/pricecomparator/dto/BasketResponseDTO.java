package com.example.pricecomparator.dto;

import com.example.pricecomparator.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasketResponseDTO {
    private String store;
    private List<Product> products;
    private double totalPrice;
}
