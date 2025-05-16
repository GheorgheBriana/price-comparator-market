package com.example.pricecomparator.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.pricecomparator.models.Discount;

@Service
public class BestDiscountsService {
    private final CSVService csvService;
    private final FileService fileService;

    //constructor
    public BestDiscountsService(CSVService csvService, FileService fileService) {
        this.csvService = csvService;
        this.fileService = fileService;
    }

    public List<Discount> getBestDiscounts(String directoryPath, String store, String date) {
        
        if(store == null || store.isBlank()) {
            throw new IllegalArgumentException("Store name is invalid");
        }
        
        if(!isValidDate(date)) {
            throw new IllegalArgumentException("Invalid date. Use the YYYY-MM-DD format");
        }

        // search all CSV files for a specific store and date
        String storePattern = store + "_discounts"; // we'll use this variable to only serch for the files that have "_discounts" in their name
        List<String> fileNames = fileService.getFileNames(directoryPath, storePattern, date);

        // verify if files were found
        if(fileNames.isEmpty()) {
            throw new IllegalStateException("There are no discounts for the store: " + store + " on date: " + date);
        }

        // get all the discounts from all files found
        List<Discount> allDiscounts = new ArrayList<>();
        for(String fileName : fileNames) {
            allDiscounts.addAll(csvService.loadDiscounts(fileName));
        }

        // sort descending by percentage
        allDiscounts.sort(Comparator.comparingDouble(Discount::getPercentageOfDiscount).reversed());

        return allDiscounts;
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
