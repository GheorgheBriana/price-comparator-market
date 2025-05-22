package com.example.pricecomparator.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String productId;
    private String productName;
    private String productCategory;
    private String brand;
    private double packageQuantity;
    private String packageUnit;
    private double price;
    private String currency;
    private String store;

        public double getPricePerBaseUnit() {
        double quantity = this.packageQuantity;
        String unit = this.packageUnit != null ? this.packageUnit.toLowerCase() : "";

        // convert to base units
        if(unit.equals("g")) {
            quantity = quantity / 1000.0; // grams to kilograms
        } else if (unit.equals("ml")) {
            quantity = quantity / 1000.0; // mililiters to liters
        }

        if(quantity == 0){
            return Double.MAX_VALUE; // avoid division by 0
        }

        double result = this.price / quantity;

        return result;

    }

    public double getUnitValue() {
        return getPricePerBaseUnit();
    }

}
