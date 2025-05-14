package com.example.pricecomparator.service;
import com.example.pricecomparator.dto.CompareDTO;
import com.example.pricecomparator.models.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompareService {

    // method to compare products from two stores
    public List<CompareDTO> compareProducts(List<Product> store1Products, List<Product> store2Products) {
        List<CompareDTO> result = new ArrayList<>();

        // map for quick lookup by productId
        Map<String, Product> store2Map = new HashMap<>();
        for(Product p : store2Products) {
            store2Map.put(p.getProductId(), p);
        }
        for(Product p1 : store1Products) {
            // search if store1 products are also in the second store
            Product p2 = store2Map.get(p1.getProductId());
            if(p2 != null) { 
                double price1 = p1.getPrice();
                double price2 = p2.getPrice();
                String cheapestStore = price1 < price2 ? "store1" : (price1 > price2 ? "store2" : "equal");

                CompareDTO dto = new CompareDTO(
                    p1.getProductId(),
                    p1.getProductName(),
                    price1,
                    price2,
                    cheapestStore
                );
                result.add(dto);
            }
        }
            return result;
    }
}
