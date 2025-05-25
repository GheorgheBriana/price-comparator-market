package com.example.pricecomparator.controller;

import com.example.pricecomparator.models.PriceAlert;
import com.example.pricecomparator.models.Product;
import com.example.pricecomparator.service.AlertService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // run Spring Boot app on a random port
public class AlertControllerIntegrationTest {

    @LocalServerPort
    private int port; // injects the actual port

    @Autowired
    private AlertService alertService; // use the real alert service (no mock)

    // create a WebTestClient for real HTTP communication with app
    private WebTestClient getClient() {
        return WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void testAlertEndpointTriggersCorrectly() {
        // register a price alert using PriceAlert object
        PriceAlert alert = new PriceAlert();
        alert.setProductId("P100");
        alert.setTargetPrice(10.0);
        alert.setStore(null); // optional: null means any store

        alertService.registerPriceAlert(alert); // use new method

        // create product with a lower price to simulate matching product from CSV
        Product product = new Product();
        product.setProductId("P100");
        product.setPrice(8.5);
        product.setStore("Lidl");
        product.setProductName("Test Product");

        // simulate that /alerts/check triggers an alert
        getClient().post() // this assumes you’ve created POST /alerts/check
            .uri("/alerts/check")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(List.of(product))
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(String.class)
            .value(result -> {
                assertThat(result).isNotEmpty(); // should trigger
                assertThat(result.get(0)).contains("Test Product"); // should mention the product
            });
    }
}
