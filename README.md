# 🛒 Price Comparator - Market

<img src="https://github.com/user-attachments/assets/79c8a0ff-b750-4e93-adea-c218fa2d3e03" alt="Banner" width="100%" />

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-app">About the App</a></li>
    <li><a href="#technologies-used">Technologies Used</a></li>
    <li><a href="#features">Features</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#how-to-run">How to Run</a></li>
      </ul>
    </li>
    <li><a href="#api-endpoints">API Endpoints</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#future-improvements">Future Improvements</a></li>
    <li><a href="#submission-details">Submission Details</a></li>
  </ol>
</details>

---

## About the App

**Price Comparator** is a Spring Boot application that helps users compare the prices of everyday grocery items across different supermarket chains (e.g., Lidl, Kaufland, Profi).  
It reads product prices and discounts from CSVs and gives access to them through REST endpoints.

---

## Technologies Used

- Language: **Java 21**
- Framework: **Spring Boot**
- Build Tool: **Maven**
- Testing: **JUnit 5**
- Data Source: **CSV files stored locally**

---

## ✅ Features
---

## I. Project Structure

### 1. `src/main/java` – Main source code

**a) controller**  
- Contains classes defining REST API endpoints.  
- `ProductsController`: lists products and recommends the best product per category.  
- `CompareController`: compares prices of the same product between two stores.  
- `BasketController`: optimizes the multi-store shopping basket for minimum cost.  
- `DiscountsController`: manages discounts (listing, top global discounts, new discounts, discount history).  
- `AlertController`: creates and verifies price alerts.

**b) service**  
Implements business logic for each feature:  
- `ProductService`: loads products from CSV files and calculates best value recommendations.  
- `CompareService`: compares prices of products common to two stores.  
- `BasketService`: optimally allocates basket products across stores to minimize total cost.  
- `DiscountService`: handles discounts, filters active and new discounts, and tracks discount history.  
- `AlertService` (implicitly via controller): manages price alerts (temporary storage and verification).  
- `FileService`: helper service for reading CSV files from resources.

**c) models**  
Represents application domain entities:  
- `Product`: product attributes such as ID, name, brand, quantity, unit, price, category, etc.  
- `Discount`: information about promotions, including discount percentage, validity period, and store.  
- `PriceAlert`: structure containing product, price threshold, and optional store for alerting.

**d) dto**  
Data Transfer Object classes for requests and responses:  
- `BasketRequestItemDTO`, `BasketResponseDTO` – for optimized shopping basket requests and responses.  
- `CompareDTO` – for price comparison results between stores.  
- `DiscountBestGlobalDTO`, `PriceHistoryDTO` – for top discounts and discount history data.  
- `BestValueRecommendationDTO` – for the best value product recommendations.

**e) exception**  
- `GlobalExceptionHandler`: intercepts unexpected errors and returns consistent JSON responses with HTTP status 500, including a timestamp and error message.

**f) `PricecomparatorApplication.java`**  
The main class annotated with `@SpringBootApplication` that starts the Spring Boot application.

---

### 2. `src/main/resources` – Static resources and configuration

- `application.properties` – Spring Boot configuration file, containing only `spring.application.name=pricecomparator` to name the application.  
- `csv/` – Directory containing CSV files with product prices and discounts for different dates and stores. These are loaded automatically at runtime from the classpath.  
  Examples:  
  - `lidl_2025-05-08.csv` – products at Lidl on a specific date.  
  - `lidl_discounts_2025-05-08.csv` – active discounts at Lidl on that date.  
  - Similar files for Kaufland, Profi, etc.

---

### 3. `src/test/java` – Tests

- Unit and integration tests for key services and controllers (e.g., `BasketServiceTest`, `DiscountServiceTest`, `BasketControllerIntegrationTest`).  
- Ensure correctness of business logic and API responses.

---

### 4. Project root – Build and configuration files

- `pom.xml` – Maven descriptor with dependencies and Java version (17).  
- Maven Wrapper files (`mvnw`, `mvnw.cmd`) enable running Maven without global install.

This structure follows standard Spring Boot conventions and all data is sourced from included CSV files without a database or external services.

---
---

## II. How to Run the Application Locally

Make sure you have the following installed:

- [Java 17+](https://adoptium.net/en-GB/temurin/releases/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- One of the following IDEs:
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)
  - [Visual Studio Code](https://code.visualstudio.com/)

### Running Steps

1. **Clone or download the project**  
   Ensure the project structure is on your disk and you are in the root directory containing the `pom.xml` file.

2. **Run using Maven Wrapper**  
   On Windows:  
   ```bash
   mvnw.cmd spring-boot:run
3. **Access the application**  
  The application starts by default on port 8080. You can access the API endpoints via a browser or a tool like Postman.
   ```bash
   GET http://localhost:8080/products/lidl/2025-05-08
CSV Files
All required data is included in the project under src/main/resources/csv.
- Product and discount data are stored as CSV files and loaded automatically at runtime from the classpath.
-  No additional manual configuration or file movement is needed.
- To add products or discounts for a new date, simply add the corresponding CSV file in the format: store_YYYY-MM-DD.csv (for products), store_discounts_YYYY-MM-DD.csv (for discounts) into the csv folder.

---
---

## III. Main Features

The application offers a variety of functionalities for comparing prices and managing discounts across multiple stores, including:

- **Listing products** from a specific store on a chosen date, with details such as price, quantity, unit of measure, and category. Data is automatically loaded from CSV files corresponding to each store and date.

- **Recommending the most cost-effective product** in a category based on the lowest price per unit (e.g., RON/kg or RON/liter). Returns a ranked list of efficient products along with a clear recommendation.

- **Comparing product prices** between two stores on a given date to identify which store offers the lowest price or if prices are equal.

- **Optimizing the shopping basket:**  
  Users submit a list of desired products (product ID and quantity), and the application calculates the optimal purchase distribution across stores to minimize total cost, factoring in active discounts.

- **Managing discounts and promotions:**  
  - Listing valid promotions in a store for a specific date.  
  - Showing the top largest active discounts across all stores.  
  - Displaying newly added discounts.  
  - Providing the history of discounts applied to a specific product for price trend analysis.

- **Price alerts:**  
  Users can set a price threshold for a product (optionally limited to a specific store). The application stores these alerts temporarily and offers a checking mechanism that generates notifications when prices fall below the set threshold.

All these features are available via REST API endpoints returning JSON, accessible through HTTP clients like browsers, Postman, or a separate frontend integration.
