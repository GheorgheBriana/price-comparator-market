package com.example.pricecomparator.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.pricecomparator.dto.BasketProductDTO;
import com.example.pricecomparator.dto.BasketRequestItemDTO;
import com.example.pricecomparator.dto.BasketResponseDTO;
import com.example.pricecomparator.models.Product;

import org.slf4j.Logger;

@Service
public class BasketService {

    private final ProductService productService;
    private final DiscountService discountService;

    private static final Logger log = LoggerFactory.getLogger(BasketService.class);

    // constructor
    public BasketService(ProductService productService, DiscountService discountService) {
        this.productService = productService;
        this.discountService = discountService;
    }

    // For each product in the user's basket, this method finds the store where it' cheapest, including active discounts.
    // The products are grouped by store to know what to buy from where.
    public Map<String, List<Product>> optimiseBasket(List<BasketRequestItemDTO> basketItems) {
        List<Product> allProducts = productService.loadAllProductsFromCsvDirectory();
        Map<String, List<Product>> groupedByStore = new HashMap<>();

        for(BasketRequestItemDTO item : basketItems) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            // find all products that match the given product id
            List<Product> matches = allProducts.stream()
                .filter(p -> p.getProductId().equalsIgnoreCase(productId))
                .toList();
            
            if(matches.isEmpty()) {
                log.warn("No product found for ID: {}", productId);
                continue;
            }

            // find the cheapest offer, considering any active discounts
            Product bestOffer = matches.stream()
                .min(Comparator.comparingDouble(p -> discountService.getDiscountedPrice(p)))
                .orElse(null);

            if(bestOffer == null ) {
                log.warn("No valid offer found for ID: {}", productId);
                continue;
            }

            log.debug("Best offer for product {} is in store {} with final price {}", productId, bestOffer.getStore(), discountService.getDiscountedPrice(bestOffer));

            // add the product multiple times, based on quantity, to the store's list
            List<Product> storeProducts = groupedByStore
                .computeIfAbsent(bestOffer.getStore(), k -> new ArrayList<>());

            for (int i = 0; i < quantity; i++) {
                storeProducts.add(bestOffer);
            }
        }

        return groupedByStore;
    }

    // This method creates the final basket response that will be returned to the client.
    // Groups the selectedproducts by store and calculates the total cost per store.
    public List<BasketResponseDTO> getOptimisedBasket(List<BasketRequestItemDTO> basketItems) {
        log.info("Optimising basket with {} item types", basketItems.size());
        
        Map<String, List<Product>> groupedByStore = optimiseBasket(basketItems); // search the most cheap item for every productId
        List<BasketResponseDTO> responseList = new ArrayList<>(); // a list with responses for every store

        // loop for every store and its optimised product list
        for(Map.Entry<String, List<Product>> entry : groupedByStore.entrySet()) {
            String store = entry.getKey(); // extracts the store name
            List<Product> products = entry.getValue(); // extracts the product list

            log.debug("Creating basket for store '{}'. {} total items", store, products.size());

        // temporary map to aggregate quantities and calculate total per product in the current store
            Map<String, BasketProductDTO> productMap = new HashMap<>();

            // for every product id => calculate the discounted price if it exists
            for(Product p : products) {
                String productId = p.getProductId();
                double unitPrice = discountService.getDiscountedPrice(p);
                
                if(!productMap.containsKey(productId)) { 
                    // if the product is not in mapping, we create a new DTO
                    productMap.put(productId, new BasketProductDTO(
                        productId,
                        p.getProductName(),
                        unitPrice,
                        1, //quantity
                        unitPrice   // initial total = unit price
                    ));
                } else { 
                    // product already exists => increment quantity and total
                    BasketProductDTO dto = productMap.get(productId);
                    dto.setQuantity(dto.getQuantity() + 1);
                    dto.setTotalPrice(dto.getTotalPrice() + unitPrice);
                }
            }

            // extract all the products from mapping
            List<BasketProductDTO> productDTOs = new ArrayList<>(productMap.values()); 

            // calculates total price
            double totalPrice = productDTOs.stream()
                .mapToDouble(BasketProductDTO::getTotalPrice)
                .sum();

            BasketResponseDTO dto = new BasketResponseDTO(store, productDTOs, totalPrice);
            responseList.add(dto);
        }

        return responseList;
    }

}
