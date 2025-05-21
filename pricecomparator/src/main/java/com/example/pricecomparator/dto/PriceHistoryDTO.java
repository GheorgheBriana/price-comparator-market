package com.example.pricecomparator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDTO {
    String from_date;
    String store;
    String percentageOfDiscount;
}
