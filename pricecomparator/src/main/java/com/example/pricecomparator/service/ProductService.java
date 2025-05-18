package com.example.pricecomparator.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

import org.springframework.stereotype.Service;

import com.example.pricecomparator.models.Product;

@Service
public class ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

        private final FileService fileService; // 1️⃣ se declară aici

    public ProductService(FileService fileService) { // 2️⃣ se injectează aici
        this.fileService = fileService;
    }

    public List<Product> loadProductsFromCsv(String filePath) {
        List<Product> products = new ArrayList<>();

        // takes files from resources
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

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

                } catch (NumberFormatException e) {
                    log.warn("Number parsing error in {}: {}", filePath, line);
                }

            }

        // exceptions
        } catch(IOException e) {
            log.error("Error reading file {}: {}", filePath, e.getMessage());
        }
        
        return products;

    }

    public List<Product> loadAllProductsFromCsvDirectory() {
        List<Product> allProducts = new ArrayList<>();

        List<String> csvFiles = fileService.getFileNames("csv", "", "");

        for (String filePath : csvFiles) {
            allProducts.addAll(loadProductsFromCsv(filePath));
        }

        return allProducts;
    }


}
