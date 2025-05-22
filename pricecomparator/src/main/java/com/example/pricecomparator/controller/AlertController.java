package com.example.pricecomparator.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pricecomparator.models.PriceAlert;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.ProductService;

import org.slf4j.Logger;

@RestController
@RequestMapping("/alerts")
public class AlertController {
    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    // temporary in-memory storage for alerts
    private static final List<PriceAlert> alerts = new ArrayList<>();

    private final ProductService productService;

    public AlertController(ProductService productService) {
        this.productService = productService;
    }

    // POST /alerts => create a new price alert
    @PostMapping
    public ResponseEntity<String> createAlert(@RequestBody PriceAlert alert) {
        log.info("Received alert request for product {} with target price {}", alert.getProductId(), alert.getTargetPrice());
        
        // add allert to the in-memory list
        alerts.add(alert);

        log.debug("Current number of alerts stored: {}", alerts.size());
        return ResponseEntity.ok("Alert added for product " + alert.getProductId() +
                " at target price " + alert.getTargetPrice());
    }

    // GET /alerts 
    // return all active alerts for testing
    @GetMapping
    public List<PriceAlert> getAllAlerts() {
        log.info("Returning all alerts. Count: {}", alerts.size());
        return alerts;
    }

    // GET /alerts/check 
    // returns products that meet price alert conditions
    @GetMapping("/check")
    public List<String> checkAlerts() {
        List<String> triggeredAlerts = new ArrayList<>();

        // log start of checking process
        log.info("Starting price alert check for {} alerts", alerts.size());

        // load all products from CSV files
        List<Product> allProducts = productService.loadAllProductsFromCsvDirectory();
        log.debug("Loaded {} products from CSV files", allProducts.size());

        // loop through each alert
        for (PriceAlert alert : alerts) {
            log.debug("Checking alert for product '{}' at target price {} in store '{}'",
                    alert.getProductId(), alert.getTargetPrice(), alert.getStore());

            for (Product product : allProducts) {

                // verify match by productId, store (optional), and price condition
                if (product.getProductId().equalsIgnoreCase(alert.getProductId())
                    && (alert.getStore() == null || product.getStore().equalsIgnoreCase(alert.getStore()))
                    && product.getPrice() <= alert.getTargetPrice()) {

                    // build notification message
                    String message = String.format(
                        "✅ Product '%s' is now %.2f RON in '%s' (target was %.2f RON)",
                        product.getProductName(),
                        product.getPrice(),
                        product.getStore(),
                        alert.getTargetPrice()
                    );

                    log.info("Alert triggered: {}", message);
                    triggeredAlerts.add(message);
                }
            }
        }

        log.info("Finished checking alerts. {} alerts were triggered.", triggeredAlerts.size());

        return triggeredAlerts;
    }


}
