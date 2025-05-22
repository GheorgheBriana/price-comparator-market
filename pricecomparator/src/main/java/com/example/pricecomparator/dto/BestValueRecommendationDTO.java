package com.example.pricecomparator.dto;

import java.util.List;
import com.example.pricecomparator.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BestValueRecommendationDTO {
    private String recommendation;
    private List<Product> products;
}
