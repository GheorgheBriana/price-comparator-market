package com.example.pricecomparator.service;

import com.example.pricecomparator.dto.DiscountBestGlobalDTO;
import com.example.pricecomparator.dto.PriceHistoryDTO;
import com.example.pricecomparator.models.Discount;
import com.example.pricecomparator.models.Product;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Service
public class DiscountService {
    private static final Logger log = LoggerFactory.getLogger(DiscountService.class);
    private final FileService fileService;

    public DiscountService(FileService fileService) {
        this.fileService = fileService;
    }
    // Loads discounts from a CSV file, parsing each line into Discount objects,
    // skipping header, malformed lines, and invalid data, then returns the list.
    public List<Discount> loadDiscountFromCsv(String filePath) {
        log.info("Loading discounts from CSV file: {}", filePath);

        List<Discount> discounts = new ArrayList<>();

        // loads file from resources
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

        // check if the file exists
        if(is == null) {
            log.warn("File not found: {}", filePath);
            return discounts;
        }

        // open file and read line by line
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        
            // extract only the filename (after last '/')
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            //extract the store name from the file name
            String storeName = fileName.contains("_") ? fileName.substring(0, fileName.indexOf("_")).toLowerCase():"unknown";

            String line;
            boolean firstLine = true; // ignore csv header
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            while ((line = br.readLine()) != null) {
                if(firstLine) {
                    firstLine = false;
                    continue;
                }

                // extract values from every csv line
                String[] fields = line.split(";");

                // verify if there are enough columns
                if(fields.length < 9) {
                    log.warn("Skipping bad line in {}: {}", filePath, line);
                    continue;
                }

                try {
                    // parse and validate discount fields
                    double packageQuantity = Double.parseDouble(fields[3].trim());
                    double percentageOfDiscount = Double.parseDouble(fields[8].trim());
                    Date fromDate = formatter.parse(fields[6].trim());
                    Date toDate = formatter.parse(fields[7].trim());

                    // valid data and positive discount
                    if(packageQuantity <= 0 || percentageOfDiscount <= 0 || !fromDate.before(toDate)) {
                        log.warn("Invalid discount values in {}: {}", filePath, line);
                        continue;
                    }

                    // create Discount DTO
                    Discount discount = new Discount(
                           fields[0].trim(),                   // productId
                            fields[1].trim(),                   // productName
                            fields[2].trim(),                   // brand
                            packageQuantity,                    // quantity
                            fields[4].trim().toLowerCase(),     // unit
                            fields[5].trim().toLowerCase(),     // category
                            fromDate,                           // fromDate
                            toDate,                             // toDate
                            percentageOfDiscount,               // percentageOfDiscount
                            storeName                           // store
                    );

                    // add the discount object in the result list
                     discounts.add(discount);

                } catch (ParseException | NumberFormatException e) {
                    log.warn("Parsing error in {}: {}", filePath, line);
                }
            }
        // catch file reading errors
        } catch (IOException e) {
            log.error("Error reading discounts file {}: {}", filePath, e.getMessage());
        }
        return discounts;
    }
    
    // Returns the list of discounts for a given store and date, sorted by highest discount first
    public List<Discount> getBestDiscounts(String directoryPath, String store, String date) {
        log.info("Getting best discounts for store={} on date={} in directory={}",
            store, date, directoryPath);

        // exception if store name is not valid
        if(store == null || store.isBlank()) {
            throw new IllegalArgumentException("Store name is invalid");
        }
        // exception if date is not valid
        if(!isValidDate(date)) {
            throw new IllegalArgumentException("Invalid date. Use the YYYY-MM-DD format");
        }
        // build file name pattern for this store’s discounts
        String pattern = store + "_discounts";
        // find all matching files in the directory for the given date
        List<String> files = fileService.getFileNames(directoryPath, pattern, date);
        
        if(files.isEmpty()) {
            throw new IllegalStateException("No discounts found for store/date");
        }

        // load and sort discounts into a single list
        List<Discount> discounts = new ArrayList<>();
        for(String file : files) {
            // add all discounts parsed from CSV file
            discounts.addAll(loadDiscountFromCsv(file));
        }
        // sort the combined list by discount percentage, highest first
        discounts.sort(Comparator.comparingDouble(Discount::getPercentageOfDiscount).reversed());
        
        return discounts;        
    }

    // Returns the list of top global discounts, filtered to only include active ones (based on current date),
    // deduplicated by productId (keeping the highest percentage), and sorted in descending order by discount percentage.
    public List<DiscountBestGlobalDTO> getGlobalTopDiscounts(String directoryPath) {
        log.info("Loading global discounts from directory: {}", directoryPath);

        // search all files that have the word "discounts" in name
        List<String> allFiles = fileService.getFileNames(directoryPath, "discounts", "");
        if (allFiles.isEmpty()) {
            log.warn("No discount files found in directory: {}", directoryPath);
            throw new IllegalStateException("No discount files found");
        }

        // get today's date for filtering active discounts
        Date today = new Date();

        // map to keep only the best discount per productId
        Map<String, DiscountBestGlobalDTO> bestDiscountsMap = new HashMap<>();

        // load all discounts from all files
        for (String file : allFiles) {
            List<Discount> discounts = loadDiscountFromCsv(file);

            for (Discount d : discounts) {
                // skip if discount is not currently active
                if (today.before(d.getFromDate()) || today.after(d.getToDate())) {
                    log.debug("Skipping inactive discount for product {} ({} - {})", d.getProductId(), d.getFromDate(), d.getToDate());
                    continue;
                }

                // build DTO
                DiscountBestGlobalDTO dto = new DiscountBestGlobalDTO(
                    d.getProductId(),
                    d.getProductName(),
                    d.getBrand(),
                    d.getPackageQuantity(),
                    d.getPackageUnit(),
                    d.getProductCategory(),
                    d.getFromDate(),
                    d.getToDate(),
                    d.getPercentageOfDiscount(),
                    d.getStore(),
                    file
                );

                // only update if this is the better discount for the same product
                DiscountBestGlobalDTO existing = bestDiscountsMap.get(d.getProductId());
                if (existing == null || dto.getPercentageOfDiscount() > existing.getPercentageOfDiscount()) {
                    bestDiscountsMap.put(d.getProductId(), dto);
                }
            }
        }

        log.info("Finished processing discounts. Unique active products: {}", bestDiscountsMap.size());

        // convert to list and sort descending by discount
        return bestDiscountsMap.values().stream()
            .sorted(Comparator.comparingDouble(DiscountBestGlobalDTO::getPercentageOfDiscount).reversed())
            .collect(Collectors.toList());
    }

    // Returns the list of discounts coming from files added in the last 24 hours.
    // Result is sorted by discount percentage in descending order.
    public List<Discount> getNewDiscounts(String directoryPath) {
        log.info("Searching for new discounts (files uploaded in the last 24 hours) from directory: {}", directoryPath);

        // Retrieve all filenames containing "discounts"
        List<String> allFiles = fileService.getFileNames(directoryPath, "discounts", "");

        // Current time and threshold for 24 hours ago
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusHours(24);

        List<Discount> newDiscounts = new ArrayList<>();

        for (String file : allFiles) {
            // Extract the file date from the name (e.g., 2025-05-24)
            LocalDate fileDate = extractFileDate(file);

            // If the file is recent (uploaded in last 24h), process it
            if (fileDate != null && fileDate.atStartOfDay().isAfter(threshold)) {
                log.debug("File {} is within the 24h threshold. Loading discounts...", file);
                newDiscounts.addAll(loadDiscountFromCsv(file));
            } else {
                log.debug("Skipping file {} - too old or invalid date", file);
            }
        }

        // Sort and return only meaningful discounts (positive %), in descending order
        List<Discount> result = newDiscounts.stream()
            .filter(d -> d.getPercentageOfDiscount() > 0)
            .sorted(Comparator.comparingDouble(Discount::getPercentageOfDiscount).reversed())
            .collect(Collectors.toList());

        log.info("Finished processing new discounts. Found {} recent discount entries.", result.size());
        return result;
    }

    // Extracts the date from filename pattern: store_discounts_YYYY-MM-DD.csv
    private LocalDate extractFileDate(String filename) {
        log.trace("Extracting date from filename: {}", filename);
        
        try {
            String datePart = filename.replaceAll(".*_(\\d{4}-\\d{2}-\\d{2})\\.csv", "$1");
            // parse the extracted segment into a LocalDate
            return LocalDate.parse(datePart);
        } catch (Exception e) {
            log.warn("Could not extract date from filename: {}", filename);
            return null;
        }
    }

    // Retrieves the price history for a specific product, filtered by optional store, brand, and category
    public List<PriceHistoryDTO> getPriceHistory(String productId, String store, String brand, String category) {
        log.info("Fetching price history for productId={}, store={}, brand={}, category={}",
                 productId, store, brand, category);
        
        // find all discount files in the 'csv' directory containing 'discounts'
        List<String> allFiles = fileService.getFileNames("csv", "discounts", "");
        
        List<PriceHistoryDTO> historyList = new ArrayList<>();
        
        // loop through all files to load discounts
        for (String file : allFiles) {
            List<Discount> discounts = loadDiscountFromCsv(file);

            for (Discount discount : discounts) {
                if (!discount.getProductId().equals(productId)) {
                    continue;
                }
                // extract filename, store name, and date parts
                String fileName = file.substring(file.lastIndexOf("/") + 1);
                String fileStore = fileName.substring(0, fileName.indexOf("_"));
                String[] parts = fileName.split("_");
                String date = parts[2].replace(".csv", "");

                // apply optional filters: store, brand, category
                if (
                    (store == null || discount.getStore().equalsIgnoreCase(store)) &&
                    (brand == null || discount.getBrand().equalsIgnoreCase(brand)) &&
                    (category == null || discount.getProductCategory().equalsIgnoreCase(category))
                ) {
                    log.debug("Adding history entry from {} on {}: {}% off",
                            fileStore, date, discount.getPercentageOfDiscount());
                    
                    // build DTO and add to result list
                     PriceHistoryDTO dto = new PriceHistoryDTO(
                        discount.getProductName(),              // productName
                        discount.getBrand(),                    // brand
                        discount.getProductCategory(),          // category
                        date,                                   // date string from filename
                        fileStore,                              // store name from filename
                        discount.getPercentageOfDiscount()      // discount percentage
                    );

                    historyList.add(dto);
                }
            }
        }

        log.info("Total history entries returned: {}", historyList.size());
        return historyList;
    }
    // Calculates the price after applying the first active discount found for the given product
    public double getDiscountedPrice(Product product) {
        log.info("Calculating discounted price for product {} at store {} (base price={})",
                 product.getProductId(), product.getStore(), product.getPrice());
        
        // find all discount files in the CSV directory
        List<String> discountFiles = fileService.getFileNames("csv", "discounts", "");
        
        // current date used to check if the discount is active
        Date now = new Date();

        // loop through each discount file and load its Discount list
        for(String file : discountFiles) {
            List<Discount> discounts = loadDiscountFromCsv(file);

            // check each discount to find a valid match
            for(Discount discount : discounts ) {
                // if this discount applies to the product and is active right now
                if(
                    // match by product id
                    discount.getProductId().equalsIgnoreCase(product.getProductId()) &&
                    // match by store
                    discount.getStore().equalsIgnoreCase(product.getStore()) &&
                    // check if the current date is within the discount period
                    now.after(discount.getFromDate()) &&
                    now.before(discount.getToDate())
                ) {
                    // calculate the discount price
                    double reduced = product.getPrice() * (1 - discount.getPercentageOfDiscount() / 100.0);

                    log.debug("Applying discount {}% for product {} from store {}. The new price is {}",
                        discount.getPercentageOfDiscount(), discount.getProductName(), discount.getStore(), reduced);
                    
                    // return the discounted price after finding a valid match
                    return reduced;
                }
            }
        }

        // no valid discount found, return original price
        log.info("No active discount found for product {} at store {}; returning original price {}",
                product.getProductId(), product.getStore(), product.getPrice());
        return product.getPrice();
    
    }

    // Method used in getBestDiscounts
    // Returns true if the string is a valid date in YYYY-MM-DD format
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
 
}

