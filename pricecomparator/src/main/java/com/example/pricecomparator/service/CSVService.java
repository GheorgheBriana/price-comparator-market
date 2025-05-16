package com.example.pricecomparator.service;

import com.example.pricecomparator.models.Discount;
import com.example.pricecomparator.models.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class CSVService {
    private static final Logger log = LoggerFactory.getLogger(CSVService.class);
    // method to read products from CSV file and return a list of Product objects
    public List<Product> loadProducts(String filePath) {
        List<Product> products = new ArrayList<>();
        
        // get file from resources folder (classpath)
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);
        
        // check if file exists
        if (is == null) {
            log.warn("File not found: {}", filePath);
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
                if (fields.length < 8) {
                    log.warn("Skipping bad line in {}: {}", filePath, line);
                    continue;
                }
                try {
                    double packageQuantity = Double.parseDouble(fields[4]);
                    double price = Double.parseDouble(fields[6]);
                    if(packageQuantity <= 0 || price < 0) {
                        log.warn("Invalid product data in {}: {}", filePath, line);
                        continue;
                    }
                        
                    Product product = new Product(
                            fields[0], // productId
                            fields[1], // productName
                            fields[2], // productCategory
                            fields[3], // brand
                            packageQuantity,
                            fields[5], // packageUnit
                            price,
                            fields[7] // currency
                    );
                    products.add(product);
            
                } catch (NumberFormatException e) {
                    log.warn("Number parsing error in {}: {}", filePath, line);
                }
            }
        } catch (IOException e) {
            log.error("Error reading file {}: {}", filePath, e.getMessage());
        }
        return products;
    }

    public List<Discount> loadDiscounts(String filePath) {
        List<Discount> discounts = new ArrayList<>();
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

        if (is == null) {
            log.warn("File not found: {}", filePath);
            return discounts;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            boolean firstLine = true;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] fields = line.split(";");
                if (fields.length < 9) continue;   // corect ar fi 9 coloane pentru modelul tău
                Discount discount = new Discount(
                        fields[0],                             // productId
                        fields[1],                             // productName
                        fields[2],                             // brand
                        Double.parseDouble(fields[3]),         // packageQuantity
                        fields[4],                             // packageUnit
                        fields[5],                             // productCategory
                        formatter.parse(fields[6]),            // fromDate
                        formatter.parse(fields[7]),            // toDate
                        Double.parseDouble(fields[8])          // percentageOfDiscount
                );
                discounts.add(discount);
            }

        } catch (IOException | ParseException e) {
            log.error("Error reading discounts file {}: {}", filePath, e.getMessage());
        }

        return discounts;
    }
}

