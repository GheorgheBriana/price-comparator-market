package com.example.pricecomparator.service;

import com.example.pricecomparator.models.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVService {

    // method to read products from CSV file and return a list of Product objects
    public List<Product> loadProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        
        // get file from resources folder (classpath)
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
        
        // check if file exists
        if (is == null) {
            System.out.println("File not found: " + filePath);
            return products;
        }

        // read file line by line
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // skip header
                    continue;
                }

                String[] fields = line.split(";");
                if (fields.length < 8) continue;  // skip bad/empty lines
                Product product = new Product(
                        fields[0], // productId
                        fields[1], // productName
                        fields[2], // productCategory
                        fields[3], // brand
                        Double.parseDouble(fields[4]), // packageQuantity
                        fields[5], // packageUnit
                        Double.parseDouble(fields[6]), // price
                        fields[7] // currency
                );
                products.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return products;
    }
}
