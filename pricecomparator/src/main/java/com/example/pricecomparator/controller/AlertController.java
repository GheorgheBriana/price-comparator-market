package com.example.pricecomparator.controller;

import com.example.pricecomparator.models.PriceAlert;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.AlertService;
import com.example.pricecomparator.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    private final ProductService productService;
    private final AlertService alertService; // use service instead of local storage

    public AlertController(ProductService productService, AlertService alertService) {
        this.productService = productService;
        this.alertService = alertService;
    }

    // POST /alerts — register a new alert
    @PostMapping
    public ResponseEntity<String> createAlert(@RequestBody PriceAlert alert) {
        log.info("Received alert for product {} at price {}", alert.getProductId(), alert.getTargetPrice());

        alertService.registerPriceAlert(alert); // use service to store alert
        return ResponseEntity.ok("Alert registered for " + alert.getProductId());
    }

    // GET /alerts — list all registered alerts (optional for debug/testing)
    @GetMapping
    public List<PriceAlert> getAllAlerts() {
        return alertService.getAllAlerts(); // fetch from service
    }

    // POST /alerts/check — allows checking alerts against a custom list of products (used in tests or manual input)
    @PostMapping("/check")
    public List<String> checkAlertsAgainstCustomProducts(@RequestBody List<Product> products) {
        log.info("Received {} products for manual alert check", products.size());
        return alertService.checkAlertsAgainstProducts(products);
    }


    // GET /alerts/check — load products from CSV and check which alerts are triggered
    @GetMapping("/check")
    public List<String> checkAlerts() {
        List<Product> allProducts = productService.loadAllProductsFromCsvDirectory(); // read from files
        return alertService.checkAlertsAgainstProducts(allProducts); // check alerts against products
    }
}
