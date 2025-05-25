package com.example.pricecomparator.service;

import com.example.pricecomparator.models.PriceAlert;
import com.example.pricecomparator.models.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {

    // In-memory list that holds all registered price alerts
    private final List<PriceAlert> alerts = new ArrayList<>();

    // Registers a new price alert into the list
    public void registerPriceAlert(PriceAlert alert) {
        alerts.add(alert);
    }

    // Returns all currently registered alerts (for GET /alerts or testing/debug)
    public List<PriceAlert> getAllAlerts() {
        return alerts;
    }

    // Checks if any of the current alerts are triggered by the provided product list
    public List<String> checkAlertsAgainstProducts(List<Product> products) {
        List<String> triggered = new ArrayList<>();

        // iterate through each registered alert
        for (PriceAlert alert : alerts) {

            // check against each product in the input list
            for (Product p : products) {
                // validate match: same productId, optional store match, price <= target
                if (p.getProductId().equalsIgnoreCase(alert.getProductId())
                        && (alert.getStore() == null || p.getStore().equalsIgnoreCase(alert.getStore()))
                        && p.getPrice() <= alert.getTargetPrice()) {

                    // format triggered message with product info
                    String message = String.format(
                            "✅ Product '%s' is now %.2f RON in '%s' (target was %.2f RON)",
                            p.getProductName(), p.getPrice(), p.getStore(), alert.getTargetPrice()
                    );

                    triggered.add(message); // add to results
                }
            }
        }

        return triggered; // return all matched alerts
    }
}
