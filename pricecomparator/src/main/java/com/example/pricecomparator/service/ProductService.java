package com.example.pricecomparator.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.pricecomparator.models.Product;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final FileService fileService;

    public ProductService(FileService fileService) { 
        this.fileService = fileService;
    }

    public List<Product> loadProductsFromCsv(String filePath) {
        List<Product> products = new ArrayList<>();

        // takes files from resources
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
        log.info("Attempting to load products from file: {}", filePath);
   
        // verify if file exists
        if(is == null) {
            log.warn("File not found: {}", filePath);
            return products;
        }

        // open file and read line by line
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        
            // extract name file
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            //extract name of the store from file
            String storeName = fileName.contains("_") ? fileName.substring(0, fileName.indexOf("_")).toLowerCase():"unknown";

            String line;
            boolean firstLine = true; // ignore csv header
            int validCount = 0;
            while((line = br.readLine()) != null) { 
                if(firstLine) {
                    firstLine = false;
                    continue;
                }

                // extract values from every csv line
                String[] fields = line.split(";");

                // verify if there are enough columns
                if(fields.length < 8) {
                    log.warn("Skipping bad line in {}: {}", filePath, line);
                    continue;
                }

                // convert values and create Product object
                try {
                    double packageQuantity = Double.parseDouble(fields[4]);
                    double price = Double.parseDouble(fields[6]);

                    // validate quantity and price
                    if(packageQuantity <= 0 || price < 0) {
                        log.warn("Invalid product data in {}: {}", filePath, line);
                        continue;
                    }

                    // create product object
                    Product product = new Product(
                        fields[0].trim(),                       // productId
                        fields[1].trim(),                       // productName
                        fields[2].trim().toLowerCase(),         // productCategory
                        fields[3].trim(),                       // brand
                        packageQuantity,                        // quantity
                        fields[5].trim().toLowerCase(),         // packageUnit
                        price,                                  // price
                        fields[7].trim().toLowerCase(),         // currency
                        storeName                               // store
                    );

                    // add object in list
                    products.add(product);
                    validCount++;

                } catch (NumberFormatException e) {
                    log.warn("Number parsing error in {}: {}", filePath, line);
                }

            }

            log.info("Loaded {} valid products from file: {}", validCount, filePath);

        // exceptions
        } catch(IOException e) {
            log.error("Error reading file {}: {}", filePath, e.getMessage());
        }
        
        return products;

    }

    public List<Product> loadAllProductsFromCsvDirectory() {
        // create a list to hold all loaded products
        List<Product> allProducts = new ArrayList<>();

        // get all CSV file paths from the resources/csv directory
        List<String> csvFiles = fileService.getFileNames("csv", "", "");
        log.info("Found {} CSV files to process", csvFiles.size());

        for (String filePath : csvFiles) {
            log.info("Processing file: {}", filePath);
            // add all products from the current file to the final list
            allProducts.addAll(loadProductsFromCsv(filePath));
        }

        log.info("Total products loaded from all CSV files: {}", allProducts.size());

        // return the completele list of products loaded from all CSV files
        return allProducts;
    }

    public List<Product> getBestValueProductsByCategory(String category, int topN) {
        log.info("Finding top {} best value products in category '{}'", topN, category);

        List<Product> allProducts = loadAllProductsFromCsvDirectory();

        return allProducts.stream()
                .filter(p -> p.getProductCategory().equalsIgnoreCase(category)) // filter only by selected category
                .sorted((p1, p2) -> Double.compare(p1.getPricePerBaseUnit(), p2.getPricePerBaseUnit())) // sort by price per base unit
                .limit(topN) // limit the number of returned products
                .peek(p -> log.debug("Included product: {} with price/unit = {}", p.getProductName(), p.getPricePerBaseUnit())) // log selected products
                .collect(Collectors.toList()); // collect the filtered and sorted products into a list
    }

}
