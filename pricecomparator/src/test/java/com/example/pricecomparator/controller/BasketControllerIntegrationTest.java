package com.example.pricecomparator.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.pricecomparator.dto.BasketRequestItemDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasketControllerIntegrationTest {
    @LocalServerPort
    private int port; // injects the port where Spring Boot application runs

    private WebTestClient webClient; // simulates HTTP requests to a Spring Boot server

    // Test a valid basket with products from two different stores.
    // Ensures the system returns a non-empty response with two separate baskets and a recommendation message
    @Test
    void testOptimiseBasket_withValidProducts() {
        // create a client that sends requests to Spring Boot application
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // create a basket with two valid products
        List<BasketRequestItemDTO> basket = List.of(
            new BasketRequestItemDTO("P001", 2),
            new BasketRequestItemDTO("P003", 1)
        );

        // send a post request to the "basket/optimise" endpoint and validate the response
        webClient.post()
            .uri("/basket/optimise")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(basket)
            .exchange()
            .expectStatus().isOk() // verify is response status is 200 OK
            .expectBody()
                .jsonPath("$.baskets").isArray() // basket is an array
                .jsonPath("$.baskets.length()").isEqualTo(2) // has two baskets
                .jsonPath("$.recommendation").exists(); // recommandation field exists

    }

    // Test a basket with a non-existent product ID.
    // Ensures the system returns an empty basket and a proper message.
    @Test
    void testOptimisedBasket_withInvalidProduct() {
        // create a client that sends requests to Spring Boot application
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // create a basket with a non-existent product ID
        List<BasketRequestItemDTO> basket = List.of(
            new BasketRequestItemDTO("UNKNOWN_ID", 1)
        );

        // send a post request to the "basket/optimise" endpoint and validate the response
        webClient.post()
            .uri("/basket/optimise")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(basket)
            .exchange()
            .expectStatus().isOk() // verify if response status is 200 OK
            .expectBody()
                .jsonPath("$.baskets").isArray() // basket is an array
                .jsonPath("$.baskets.length()").isEqualTo(0) // no valid products found
                .jsonPath("$.recommendation").value(msg -> ((String) msg).contains("No products")); // recommendation message contains "No products"
    }

    // Test an empty basket request.
    // Ensures the system returns a valid response with no baskets.
    @Test
    void testOptimisedBasket_withEmptyList() {
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        webClient.post()
            .uri("/basket/optimise")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(List.of()) // empty basket
            .exchange()
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.baskets").isArray()
                .jsonPath("$.baskets.length()").isEqualTo(0)
                .jsonPath("$.recommendation").value(msg -> ((String) msg).contains("No products"));
    }


}
