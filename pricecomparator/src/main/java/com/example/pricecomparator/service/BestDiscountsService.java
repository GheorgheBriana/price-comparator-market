package com.example.pricecomparator.service;

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
        List<String> fileNames = fileService.getFileNames(directoryPath, store, date);

        if(fileNames.isEmpty()) {
            throw new RuntimeException("No files found for the specified store and date.");
        }

        List<Discount> allDiscounts = new ArrayList<>();

        for(String fileName : fileNames) {
            List<Discount> discounts = csvService.loadDiscounts(fileName);
            allDiscounts.addAll(discounts);
        }

        allDiscounts.sort(Comparator.comparingDouble(Discount::getPercentageOfDiscount).reversed());

        return allDiscounts;
    }


}
