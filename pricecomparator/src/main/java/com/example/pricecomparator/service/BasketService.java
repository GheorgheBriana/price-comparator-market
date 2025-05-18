package com.example.pricecomparator.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import com.example.pricecomparator.models.Product;

@Service
public class BasketService {

    private final ProductService productService;

    public BasketService(ProductService productService) {
        this.productService = productService;
    }

    public Map<String, List<Product>> optimiseBasket(List<BasketRequestItemDTO> basketItems) {
        List<Product> allProducts = productService.loadAllProductsFromCsvDirectory();
        Map<String, List<Product>> groupedByStore = new HashMap<>();

        for(BasketRequestItemDTO item : basketItems) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            // search products with the same productId
            List<Product> matches = allProducts.stream().filter(p-> p.getProductId().equalsIgnoreCase(productId)).toList();
            if(matches.isEmpty()) continue;
            
            // find the cheapest price
            Product bestOffer = matches.stream()
                .min(Comparator.comparingDouble(Product::getPrice))
                .orElse(null);
            if(bestOffer == null ) continue;

        List<Product> storeProducts = groupedByStore
            .computeIfAbsent(bestOffer.getStore(), k -> new ArrayList<>());

        for (int i = 0; i < quantity; i++) {
            storeProducts.add(bestOffer);
        }

        }
        return groupedByStore;
    }

    public List<BasketResponseDTO> getOptimisedBasket(List<BasketRequestItemDTO> basketItems) {
        Map<String, List<Product>> groupedByStore = optimiseBasket(basketItems);
        List<BasketResponseDTO> responseList = new ArrayList<>();

        for(Map.Entry<String, List<Product>> entry : groupedByStore.entrySet()) {
            String store = entry.getKey();
            List<Product> products = entry.getValue();

            // calculates total price
            double totalPrice = products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
            
            BasketResponseDTO dto = new BasketResponseDTO(store, products, totalPrice);
            responseList.add(dto);
        }

        return responseList;
    }

}
