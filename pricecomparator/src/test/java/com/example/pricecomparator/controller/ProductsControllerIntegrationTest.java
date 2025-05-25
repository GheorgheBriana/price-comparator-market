package com.example.pricecomparator.controller;

import com.example.pricecomparator.dto.BestValueRecommendationDTO;
import com.example.pricecomparator.models.Product;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductsControllerIntegrationTest {

    @LocalServerPort
    private int port; // injects the port where the app is running

    private WebTestClient webClient; // simulates HTTP requests to the Spring Boot app

    // Test GET /products/best-value?category=lactate&top=3
    // Should return top 3 products in 'lactate' category sorted by value per unit
    @Test
    void testGetBestValueProducts_validCategory_returnsSortedListAndMessage() {
        // create test client
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        // send GET request to endpoint with valid params
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/best-value")
                        .queryParam("category", "lactate")
                        .queryParam("top", 3)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk() // expect HTTP 200 OK
                .expectBody(BestValueRecommendationDTO.class) // deserialize into DTO
                .value(response -> {
                    // assert that response message is not null
                    assertThat(response.getRecommendation()).isNotEmpty();

                    // assert list contains products
                    List<Product> products = response.getProducts();
                    assertThat(products).isNotNull();
                    assertThat(products).isNotEmpty();

                    // assert list is sorted by value per base unit
                    for (int i = 1; i < products.size(); i++) {
                        double prev = products.get(i - 1).getPricePerBaseUnit();
                        double current = products.get(i).getPricePerBaseUnit();
                        assertThat(prev).isLessThanOrEqualTo(current);
                    }
                });
    }

    // Test GET /products/best-value with unknown category
    // Should return HTTP 204 No Content
    @Test
    void testGetBestValueProducts_invalidCategory_returnsNoContent() {
        // create test client
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/best-value")
                        .queryParam("category", "unknown-category")
                        .queryParam("top", 5)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent(); // expect HTTP 204
    }

    // Test GET /products/{productId}/substitutes?top=3&sameBrand=false
    // Should return substitute products from the same category (optionally same brand), sorted by best value
    @Test
    void testGetProductSubstitutes_validProductId_returnsSubstitutes() {
        // create test client
        webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();

        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/products/{productId}/substitutes")
                        .queryParam("top", 3)
                        .queryParam("sameBrand", false)
                        .build("P001"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk() // HTTP 200 OK
                .expectBodyList(Product.class)
                .value(substitutes -> {
                    assertThat(substitutes).isNotNull();
                    assertThat(substitutes).isNotEmpty();

                    // assert same category as original product
                    String originalCategory = substitutes.get(0).getProductCategory();
                    for (Product substitute : substitutes) {
                        assertThat(substitute.getProductCategory()).isEqualToIgnoringCase(originalCategory);
                    }
                });
    }
}
