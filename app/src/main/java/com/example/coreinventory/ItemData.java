package com.example.coreinventory;

import java.text.DecimalFormat;

public class ItemData {

    private String itemCode;
    private String itemName;
    private int itemQuant;
    private double itemPrice;

    private static DecimalFormat df = new DecimalFormat("#.##");


    public ItemData(){
    }

    public ItemData(String code, String name, int quant, double price){
        this.itemCode = code;
        this.itemName = name;
        this.itemQuant = quant;
        this.itemPrice = price;
    }

    public String getItemName() {

        return itemName;
    }

    public int getItemQuant() {

        return itemQuant;
    }

    public double getItemPrice() {

        return itemPrice;
    }

    public String getItemCode() {

        return itemCode;
    }

    public void setItemCode(String itemCode) {

        this.itemCode = itemCode;
    }

    public void setItemName(String itemName) {

        this.itemName = itemName;
    }

    public void setItemQuant(int itemQuant) {

        this.itemQuant = itemQuant;
    }

    public void setItemPrice(double itemPrice) {

        this.itemPrice = itemPrice;
    }

    @Override
    public String toString() {
        return "Item Code: " + itemCode + '\n' +
                "Item Name: " + itemName + '\n' +
                "Item Quantity: " + itemQuant + '\n' +
                "Item Price: £" + df.format(itemPrice);
    }

}
