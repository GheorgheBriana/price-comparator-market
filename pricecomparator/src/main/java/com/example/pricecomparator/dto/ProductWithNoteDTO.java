package com.example.pricecomparator.dto;

import com.example.pricecomparator.models.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithNoteDTO {
    private Product product;
    private String note;

}

