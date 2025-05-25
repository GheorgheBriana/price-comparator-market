// DTO used to return a recommendation for best value products
// contains a message and a list of suggested products
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
    private String recommendation; // message explaining why these are the best
    private List<Product> products;
}
