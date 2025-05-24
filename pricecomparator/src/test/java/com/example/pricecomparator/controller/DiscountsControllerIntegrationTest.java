package com.example.pricecomparator.controller;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscountsControllerIntegrationTest {
    
    @LocalServerPort
    private int port; // injects the port where Spring Boot application runs

    private WebTestClient webClient; // simulates HTTP requests to a Spring Boot server

    @Test
    void testGetGlobalBestDiscounts_returnsActiveAndSortedDiscounts() {
        // create client
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // call endpoint and verify results
        webClient.get()
            .uri("/discounts/best-global") // tested endpoint
            .accept(MediaType.APPLICATION_JSON) // JSON response
            .exchange() // sends request
            .expectStatus().isOk()
            .expectBody()
                .jsonPath("$.length()").value(length -> {
                    assertThat((Integer) length).isGreaterThan(0);
                }) // verify if the list is not empty
                .jsonPath("$[0].percentageOfDiscount").exists() // percentageOfDiscount exists for first two items
                .jsonPath("$[1].percentageOfDiscount").exists();
    }

    @Test
    void testGetGlobalBestDiscounts_returnsNoContentWhenEmpty() {
        // create client
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // call endpoint and verify response status is 204 No Content
        webClient.get()
            .uri("/discounts/best-global") // tested endpoint
            .accept(MediaType.APPLICATION_JSON) // expects JSON (even if empty)
            .exchange() // sends request
            .expectStatus().isNoContent(); // verify if response status is 204 No Content
    }
    
}
