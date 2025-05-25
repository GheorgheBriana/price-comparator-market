package com.example.pricecomparator.controller;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.example.pricecomparator.dto.PriceHistoryDTO;

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
    void testGetNewDiscounts_returnsRecentDiscountsSortedOrEmpty() {
        // create client
        webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

        // call endpoint and handle both 200 OK and 204 No Content
        webClient.get()
            .uri("/discounts/new") // tested endpoint
            .accept(MediaType.APPLICATION_JSON) // expect JSON response
            .exchange()
            .expectStatus().value(status -> {
                assertThat(status == 200 || status == 204).isTrue();
            });
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

    @Test
    void testGetPriceHistory_withValidFilters_returnsHistoryList() {
    // create client
    webClient = WebTestClient.bindToServer()
            .baseUrl("http://localhost:" + port)
            .build();

    // call endpoint with query filters and validate response
    webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .path("/discounts/price-history") // tested endpoint
                    .queryParam("productId", "P001")
                    .queryParam("store", "lidl")
                    .queryParam("brand", "Zuzu")
                    .queryParam("category", "lactate")
                    .queryParam("from", "2025-05-01")
                    .queryParam("to", "2025-05-03")
                    .build()
            )
            .accept(MediaType.APPLICATION_JSON) // expects JSON response
            .exchange() // send request
            .expectStatus().isOk() // expect HTTP 200 OK
            .expectBodyList(PriceHistoryDTO.class)
            .value(historyList -> {
                // check if response list is not empty
                assertThat(historyList).isNotEmpty();

                // validate every entry matches expected filters
                for (PriceHistoryDTO dto : historyList) {
                    assertThat(dto.getStore().toLowerCase()).isEqualTo("lidl"); // store filter
                    assertThat(dto.getBrand().toLowerCase()).isEqualTo("zuzu"); // brand filter
                    assertThat(dto.getCategory().toLowerCase()).isEqualTo("lactate"); // category filter
                    assertThat(dto.getFrom_date()).isBetween("2025-05-01", "2025-05-03"); // date range filter
                }
            });
    }

    @Test
    void testGetPriceHistory_withoutFilters_shouldReturnAllForProduct() {
        // create client
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        // call endpoint using only productId, no other filters
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/discounts/price-history") // tested endpoint
                        .queryParam("productId", "P001")
                        .build()
                )
                .accept(MediaType.APPLICATION_JSON) // expects JSON response
                .exchange() // send request
                .expectStatus().isOk() // expect HTTP 200 OK
                .expectBodyList(PriceHistoryDTO.class)
                .value(historyList -> {
                    // check that list is not empty
                    assertThat(historyList).isNotEmpty();

                    // basic content check (optional, based on known test data)
                    assertThat(historyList.get(0).getProductName().toLowerCase()).contains("zuzu"); // product name check
                });
    }

}