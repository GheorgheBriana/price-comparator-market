package com.example.pricecomparator.service;

import com.example.pricecomparator.dto.DiscountBestGlobalDTO;
import com.example.pricecomparator.dto.PriceHistoryDTO;
import com.example.pricecomparator.models.Discount;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
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

    public List<Discount> loadDiscountFromCsv(String filePath) {
        List<Discount> discounts = new ArrayList<>();

        // takes file from resources
        InputStream is = getClass().getClassLoader().getResourceAsStream(filePath);

        // verify if file exists
        if(is == null) {
            log.warn("File not found: {}", filePath);
            return discounts;
        }

        // open file and read line by line
        try(BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
        
            // extract name file
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

            //extract name of the store from file
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
                    
                    // converting and validations
                    double packageQuantity = Double.parseDouble(fields[3].trim());
                    double percentageOfDiscount = Double.parseDouble(fields[8].trim());
                    Date fromDate = formatter.parse(fields[6].trim());
                    Date toDate = formatter.parse(fields[7].trim());

                    // valid data and positive discount
                    if(packageQuantity <= 0 || percentageOfDiscount <= 0 || !fromDate.before(toDate)) {
                        log.warn("Invalid discount values in {}: {}", filePath, line);
                        continue;
                    }

                    // create Discount object
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

                    // add object in list
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

    public List<Discount> getBestDiscounts(String directoryPath, String store, String date) {

        // exception if store name is not valid
        if(store == null || store.isBlank()) {
            throw new IllegalArgumentException("Store name is invalid");
        }
        // exception if date is not valid
        if(!isValidDate(date)) {
            throw new IllegalArgumentException("Invalid date. Use the YYYY-MM-DD format");
        }
        // search files
        String pattern = store + "_discounts";
        List<String> files = fileService.getFileNames(directoryPath, pattern, date);
        if(files.isEmpty()) {
            throw new IllegalStateException("No discounts found for store/date");
        }
        // load and sort discounts
        List<Discount> discounts = new ArrayList<>();
        for(String file : files) {
            discounts.addAll(loadDiscountFromCsv(file));
        }
        discounts.sort(Comparator.comparingDouble(Discount::getPercentageOfDiscount).reversed());
        return discounts;        
    }

    public List<DiscountBestGlobalDTO> getGlobalTopDiscounts(String directoryPath) {
        // search all files that have the word "discounts" in name
        List<String> allFiles = fileService.getFileNames(directoryPath, "discounts", "");
        if(allFiles.isEmpty()) {
            throw new IllegalStateException("No discount files found");
        }
        // load all discounts from all files
        List<DiscountBestGlobalDTO> allDiscounts = new ArrayList<>();
        for (String file : allFiles) { 
            List<Discount> discounts = loadDiscountFromCsv(file);
            for (Discount d : discounts) {
                allDiscounts.add(new DiscountBestGlobalDTO(
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
                ));
            }
        }

        // sort discounts by percentage in descending order
        allDiscounts.sort(Comparator.comparingDouble(DiscountBestGlobalDTO::getPercentageOfDiscount).reversed());
        return allDiscounts;
    }

    public List<Discount> getNewDiscounts(String directoryPath) {
    List<String> allFiles = fileService.getFileNames(directoryPath, "discounts", "");
    List<Discount> allDiscounts = new ArrayList<>();

    for (String file : allFiles) {
        allDiscounts.addAll(loadDiscountFromCsv(file));
    }
        List<Discount> newDiscounts = new ArrayList<>();

        LocalDate currentDate = LocalDate.now();
        LocalDate yesterday = currentDate.minusDays(1);

        ZoneId zoneId = ZoneId.systemDefault();
        for (Discount discount : allDiscounts) {
            if (discount.getFromDate() != null) {
                LocalDate fromDate = discount.getFromDate()
                                            .toInstant()
                                            .atZone(zoneId)
                                            .toLocalDate();

                if (!fromDate.isBefore(yesterday)) {
                    newDiscounts.add(discount);
                }
            }
        }
        return newDiscounts;
    
    }

    public List<PriceHistoryDTO> getPriceHistory(String productId, String store, String brand, String category) {
        List<String> allFiles = fileService.getFileNames("csv", "discounts", "");
        List<PriceHistoryDTO> historyList = new ArrayList<>();
        
        for (String file : allFiles) {
            List<Discount> discounts = loadDiscountFromCsv(file);

            for (Discount discount : discounts) {
                if (discount.getProductId().equals(productId)) {
                    String fileName = file.substring(file.lastIndexOf("/") + 1);
                    String fileStore = fileName.substring(0, fileName.indexOf("_"));
                    String[] parts = fileName.split("_");
                    String date = parts[2].replace(".csv", "");

                    if (
                        (store == null || discount.getStore().equalsIgnoreCase(store)) &&
                        (brand == null || discount.getBrand().equalsIgnoreCase(brand)) &&
                        (category == null || discount.getProductCategory().equalsIgnoreCase(category))
                    ) {
                        PriceHistoryDTO dto = new PriceHistoryDTO(date, fileStore, discount.getPercentageOfDiscount());
                        historyList.add(dto);
                    }
                }
            }
        }
        
        return historyList;
    }


    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
 
}


