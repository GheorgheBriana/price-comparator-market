# 🛒 Price Comparator - Market

<img src="https://github.com/user-attachments/assets/79c8a0ff-b750-4e93-adea-c218fa2d3e03" alt="Banner" width="100%" />

<!-- TABLE OF CONTENTS -->
<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-app">About the App</a></li>
    <li><a href="#technologies-used">Technologies Used</a></li>
    <li><a href="#features">Features</a></li>
    <li>
      <a href="#i-project-structure">I. Project Structure</a>
      <ul>
        <li><a href="#1-srcmainjava--main-source-code">1. src/main/java – Main source code</a></li>
        <li><a href="#2-srcmainresources--static-resources-and-configuration">2. src/main/resources – Static resources and configuration</a></li>
        <li><a href="#3-srctestjava--tests">3. src/test/java – Tests</a></li>
        <li><a href="#4-project-root--build-and-configuration-files">4. Project root – Build and configuration files</a></li>
      </ul>
    </li>
    <li>
      <a href="#ii-how-to-run-the-application-locally">II. How to Run the Application Locally</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#running-steps">Running Steps</a></li>
      </ul>
    </li>
    <li><a href="#iii-main-features">III. Main Features</a></li>
    <li>
      <a href="#api-endpoints">API Endpoints</a>
      <ul>
        <li><a href="#1-products">1. Products</a></li>
        <li><a href="#2-price-comparison">2. Price Comparison</a></li>
        <li><a href="#3-optimized-shopping-basket">3. Optimized Shopping Basket</a></li>
        <li><a href="#4-discounts-and-promotions">4. Discounts and Promotions</a></li>
        <li><a href="#5-price-alerts">5. Price Alerts</a></li>
      </ul>
    </li>
    <li><a href="#v-assumptions-and-simplifications-in-implementation">V. Assumptions and Simplifications in Implementation</a></li>
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

## Features
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

- [Java 17+]([https://adoptium.net/en-GB/temurin/releases/](https://www.oracle.com/ro/java/technologies/downloads/))
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

## 1. Products

### `GET /products/{store}/{date}`  
Returns the full list of products available in the specified store on the given date.

**Path parameters:**

| Parameter | Required | Description                             | Example        |
|-----------|----------|-----------------------------------------|----------------|
| `store`   | ✔        | Store name (`lidl`, `kaufland`, etc.)   | `lidl`         |
| `date`    | ✔        | Date in `YYYY-MM-DD` format             | `2025-05-08`   |

**Example:**  
`GET /products/lidl/2025-05-08`

![image](https://github.com/user-attachments/assets/8e9e25e9-621b-4ed9-bccb-00c049c4a73a)

---

### `GET /products/best-value`  
Provides recommendations for products with the lowest price per unit.

**Query parameters:**

| Parameter  | Required | Description                                  | Example                |
|------------|----------|----------------------------------------------|------------------------|
| `category` | ✔        | Product category (`lactate`, `bauturi`, ...) | `lactate`              |
| `top`      | ✖        | Number of products to return (default: 5)     | `5`                    |

**Example:**  
`GET /products/best-value?category=lactate&top=5`

![image](https://github.com/user-attachments/assets/b18400fa-9b48-4cac-8b51-c518eb99a27c)

---

## 2. Price Comparison

### `GET /compare/{store1}/{date1}/{store2}/{date2}`  
Compares prices of products common to two stores.

**Path parameters:**

| Parameter   | Required | Description             | Example           |
|-------------|----------|-------------------------|-------------------|
| `store1`    | ✔        | First store name        | `lidl`            |
| `date1`     | ✔        | First store date        | `2025-05-01`      |
| `store2`    | ✔        | Second store name       | `kaufland`        |
| `date2`     | ✔        | Second store date       | `2025-05-01`      |

**Example:**  
`GET /compare/lidl/2025-05-01/kaufland/2025-05-01`

![image](https://github.com/user-attachments/assets/ea3bad5b-d48f-489f-a7dc-b0d4c7d36f16)

---

## 3. Optimized Shopping Basket

### `POST /basket/optimise`  
Calculates the optimal way to purchase products from multiple stores to minimize total cost.

**Request body:**  
A JSON array of products, each with:

| Field       | Required | Description               | Example  |
|-------------|----------|---------------------------|----------|
| `productId` | ✔        | ID of the product         | `P001`   |
| `quantity`  | ✔        | Quantity to purchase      | `2`      |

**Example body:**
```json
[
  { "productId": "P001", "quantity": 2 },
  { "productId": "P005", "quantity": 1 }
]
```

**Response:**  
A JSON object with:
- A recommendation message (e.g., "Basket optimized across 2 stores")
- A list of baskets per store, each with products, prices, and totals

![image](https://github.com/user-attachments/assets/bcab2d7b-ee84-4749-817d-174745f58558)

---

## 4. Discounts and Promotions

### `GET /discounts/{store}/{date}`  
Returns the list of active discounts in the specified store on a given date.

**Path parameters:**

| Parameter | Required | Description               | Example        |
|-----------|----------|---------------------------|----------------|
| `store`   | ✔        | Store name                | `profi`        |
| `date`    | ✔        | Date in `YYYY-MM-DD`      | `2025-05-08`   |

**Example:**  
`GET /discounts/profi/2025-05-08`

![image](https://github.com/user-attachments/assets/c89386d3-3c3f-435d-b67a-a4e688584fb2)

---

### `GET /discounts/best-global`  
Returns the top discounts (by percentage) from all stores, based on the current date.

![image](https://github.com/user-attachments/assets/c58c6dfd-ee16-4c78-989c-5310dd8cf13a)

---

### `GET /discounts/new`  
Lists newly added discounts that started today or yesterday.

- Returns list of new discounts, or  
- HTTP 204 No Content if none found

![image](https://github.com/user-attachments/assets/ca8e45fb-d515-4a84-9715-fe9af787d4ad)

---

### `GET /discounts/price-history`  
Returns discount and price history for one or more products.

**Query parameters (all optional):**

| Parameter     | Description                                     |
|---------------|-------------------------------------------------|
| `productId`   | Filter by product ID                            |
| `store`       | Filter by store name (`lidl`, `kaufland`, etc.)|
| `brand`       | Filter by product brand                         |
| `category`    | Filter by product category                      |
| `from`        | Start date (inclusive) – `YYYY-MM-DD`           |
| `to`          | End date (inclusive) – `YYYY-MM-DD`             |

**Example:**  
`GET /discounts/price-history?productId=P002`

![image](https://github.com/user-attachments/assets/a1eede1c-b0a1-49ee-93f6-0738488b1c50)

---

## 5. Price Alerts

### `POST /alerts`  
Creates a new price alert stored temporarily in memory.

**Request body parameters:**

| Field         | Required | Description                                                                 |
|---------------|----------|-----------------------------------------------------------------------------|
| `productId`   | ✔        | The ID of the product to monitor                                            |
| `targetPrice` | ✔        | The price threshold (when the product reaches or falls below this price)   |
| `store`       | ✖        | Optional store name (if omitted, alert applies to all stores)              |

**Example:**
`{
  "productId": "P005",
  "targetPrice": 14.0,
  "store": "Lidl"
}`

![image](https://github.com/user-attachments/assets/94f9bf8a-13d7-4d14-a9ce-077b62b3736d)

### `GET /alerts`  
Returns a list of all active price alerts currently stored in memory.

![image](https://github.com/user-attachments/assets/8405c9f3-0719-46c2-8143-bbf3a4c0c005)

## `GET /alerts/check`
Manually checks all registered alerts against the current product prices.
Returns a list of messages for alerts that have been triggered.

If no alerts are triggered, the response will be an empty list or HTTP 204 No Content.

![image](https://github.com/user-attachments/assets/59279a2a-7221-449e-8ee7-b615943c8e02)


## V. Assumptions and Simplifications in Implementation

During the development of this project, several assumptions and simplifications were made to limit the application’s complexity:

- **Data stored in files, without a database:**  
  The application does not use a relational database or any persistent storage system. All product and discount information is read directly from CSV files located in the `resources` folder. Data updates are made by replacing or adding CSV files. During runtime, data is kept only temporarily in memory and reloaded on each request without caching.

- **Global unique product identifier:**  
  It is assumed that `productId` (the product code) is globally unique and consistent across different stores. This enables price comparison between stores (via the `/compare` endpoint) and basket optimization. Although in reality stores may have different internal codes for the same item, this project uses a common ID for simplification (e.g., `P001` represents the same product in Lidl, Kaufland, etc.).

- **No authentication or user roles:**  
  All endpoints are unsecured; no authentication or authorization is implemented. Any user can access the API directly. In a real-world application, security would be essential for sensitive operations but was omitted here for simplicity.

- **Volatile storage for price alerts:**  
  Price alerts created via `/alerts` are stored only in application memory (an in-memory static list). Alerts are not persisted to a database or file, so they are lost if the server restarts.

- **Manual alert checking:**  
  There is no automated process (cron job, real-time notifications) to alert users when a price alert condition is met. Users must explicitly call `/alerts/check` to verify if any alerts are triggered. This simplified model could be enhanced with schedulers, WebSocket, or email notifications in a production system.

- **Data and logic specific to demo context:**  
  The CSV file structure and application logic reflect specific requirements. For example, CSV files expect semicolon delimiters and exact column order. Discounts are considered “new” if their start date is today or very recent, and “active” if the current date falls within the promotion interval. Complex scenarios like overlapping promotions or new products without history are not fully handled.

- **Basket optimization ignores external factors:**  
  The optimization algorithm treats each product independently, selecting the store offering the lowest (possibly discounted) price. It does not consider transport costs, store stock limits, or other constraints. This approach focuses on demonstrating core logic; real applications would require more complex factors.

- **Fixed date and input formats:**  
  The application assumes dates in `YYYY-MM-DD` format in URLs and files. Input validation is minimal — invalid dates throw exceptions caught by a global handler that returns generic error messages. Store names in URLs must exactly match the CSV naming (e.g., `lidl`, `kaufland` in lowercase). Robust validation could improve usability.

- **No graphical user interface (UI):**  
  The project only provides a backend REST API. Interactions occur solely via HTTP requests and JSON responses. No frontend or web pages are included. A UI would be developed separately or accessed via third-party tools (e.g., Postman).

---

These clarifications highlight that certain aspects were simplified for demonstration purposes. Extending the project for production would require adding security, persistent storage, advanced validation, and possibly UI components. Nonetheless, the implemented features provide a solid foundation for price comparison and discount management as initially required.
