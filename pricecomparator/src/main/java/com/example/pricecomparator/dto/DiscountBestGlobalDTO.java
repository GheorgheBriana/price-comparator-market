// DTO used to represent the best active discount for a product

package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DiscountBestGlobalDTO{
    private String productId;
    private String productName;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private String productCategory;
    private Date fromDate;
    private Date toDate;
    private double percentageOfDiscount;
    private String store;
    private String sourceFile;
}
