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

import org.slf4j.Logger;

@RestController
@RequestMapping("/alerts")
public class AlertController {
    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    // temporary in-memory storage for alerts
    private static final List<PriceAlert> alerts = new ArrayList<>();

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
}
