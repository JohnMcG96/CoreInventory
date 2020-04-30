package com.example.coreinventory;

import java.text.DecimalFormat;
import java.util.Date;

public class ItemData {

    private String itemCode;
    private String itemName;
    private int itemQuant;
    private double itemPrice;
    private String itemLoc;
    private String itemDatePur;
    private boolean itemThird;
    private boolean itemCheck;

    private static DecimalFormat df = new DecimalFormat("#.##");


    public ItemData(){
    }

    public ItemData(String code, String name, int quant, double price, String loc, String itemDatePur,
                    boolean itemThird, boolean itemCheck){
        this.itemCode = code;
        this.itemName = name;
        this.itemQuant = quant;
        this.itemPrice = price;
        this.itemLoc = loc;
        this.itemDatePur = itemDatePur;
        this.itemThird = itemThird;
        this.itemCheck = itemCheck;
    }

    public String getItemCode() {

        return itemCode;
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

    public String getItemLoc() {

        return itemLoc;
    }

    public String getItemDatePur() {

        return itemDatePur;
    }

    public boolean getItemThird() {

        return itemThird;
    }

    public boolean getItemCheck() {

        return itemCheck;
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

    public void setItemLoc(String itemLoc) {

        this.itemLoc = itemLoc;
    }

    public void setItemDatePur(String itemDatePur) {

        this.itemDatePur = itemDatePur;
    }

    public void setItemThird(boolean itemThird) {

        this.itemThird = itemThird;
    }

    public void setItemCheck(boolean itemCheck) {

        this.itemCheck = itemCheck;
    }

    @Override
    public String toString() {
        return "Item Code: " + itemCode + '\n' +
                "Item Name: " + itemName + '\n' +
                "Item Quantity: " + itemQuant + '\n' +
                "Item Price: £" + df.format(itemPrice) + '\n' +
                "Item Location: " + itemLoc + '\n' +
                "Item Date Purchased: " + itemDatePur + '\n' +
                "Item Third Party: " + itemThird + '\n' +
                "Item Checked: " + itemCheck;
    }

}
