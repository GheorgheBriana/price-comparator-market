package com.example.pricecomparator.service;

import com.example.pricecomparator.models.PriceAlert;
import com.example.pricecomparator.models.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AlertServiceTest {

    private final AlertService alertService = new AlertService();

    @Test
    void testAlertTriggeredWhenPriceDropsBelowTarget() {
        // create and register a new price alert (P001, 5.0 RON)
        PriceAlert alert = new PriceAlert();
        alert.setProductId("P001");
        alert.setTargetPrice(5.0);
        alert.setStore(null); // no store restriction
        alertService.registerPriceAlert(alert);

        // simulate a product with price lower than threshold
        Product product = new Product();
        product.setProductId("P001");
        product.setPrice(4.5);
        product.setProductName("Test Product");
        product.setStore("Lidl");

        // check if alert is triggered correctly
        List<String> triggered = alertService.checkAlertsAgainstProducts(List.of(product));

        // one alert expected, and message must contain product name
        assertEquals(1, triggered.size());
        assertTrue(triggered.get(0).contains("Test Product"));
    }

    @Test
    void testNoAlertTriggeredWhenPriceTooHigh() {
        // register alert
        PriceAlert alert = new PriceAlert();
        alert.setProductId("P001");
        alert.setTargetPrice(5.0);
        alert.setStore(null);
        alertService.registerPriceAlert(alert);

        // create product with price above the threshold
        Product product = new Product();
        product.setProductId("P001");
        product.setPrice(6.0);
        product.setProductName("Test Product");
        product.setStore("Lidl");

        // no alert should be triggered
        List<String> triggered = alertService.checkAlertsAgainstProducts(List.of(product));

        assertTrue(triggered.isEmpty());
    }

    @Test
    void testAlertTriggeredOnlyWhenStoreMatches() {
        // alert is specific to 'Auchan'
        PriceAlert alert = new PriceAlert();
        alert.setProductId("P001");
        alert.setTargetPrice(5.0);
        alert.setStore("Auchan");
        alertService.registerPriceAlert(alert);

        // product is in Lidl — should NOT trigger
        Product product = new Product();
        product.setProductId("P001");
        product.setPrice(4.0);
        product.setProductName("Test Product");
        product.setStore("Lidl");

        List<String> triggered = alertService.checkAlertsAgainstProducts(List.of(product));
        assertTrue(triggered.isEmpty());

        // product is in Auchan — SHOULD trigger
        product.setStore("Auchan");
        List<String> triggered2 = alertService.checkAlertsAgainstProducts(List.of(product));
        assertEquals(1, triggered2.size());
    }
}
