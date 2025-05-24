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

     // this test fails if we dont have new discounts csv files in resources folder
    @Test
    void testGetNewDiscounts_returnsRecentDiscountsSorted() {
        // create client
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();
        
        // call endpoint and verify results
        webClient.get()
            .uri("/discounts/new") // tested endpoint
            .accept(MediaType.APPLICATION_JSON) // expect JSON response
            .exchange() // sends the request
            .expectStatus().isOk()// expect 200 OK
            .expectBody()
                .jsonPath("$.length()").value(length -> {
                    assertThat((Integer) length).isGreaterThan(0);
                }) // verify the list is not empty
                .jsonPath("$[0].percentageOfDiscount").exists(); // check that discount field exists
    }


    // this test fails if we have new discounts csv files in resources folder

    // @Test
    // void testGetNewDiscounts_returns204WhenNoContent() {
    //     webClient = WebTestClient.bindToServer()
    //         .baseUrl("http://localhost:" + port)
    //         .build();

    //     webClient.get()
    //         .uri("/discounts/new")
    //         .accept(MediaType.APPLICATION_JSON)
    //         .exchange()
    //         .expectStatus().isNoContent(); // 204
    // }


}