package com.example.android.inventorymanager.data;

/**
 * Created by Roy Li on 20/12/2017.
 */

public class Stock {
    private long productID;
    private String productName;
    private String price;
    private int quantity;
    private String image;

    public Stock() {}

    public Stock(long id, String productName, String price, int quantity, String image) {
        this.productID = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }



    public String getProductName() {
        return productName;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImage() {
        return image;
    }

    public long getProductID() {
        return productID;
    }

    public void setImage(String s) {
        image = s;
    }
    @Override
    public String toString() {
        return "StockItem{" +
                "productID='" + productID + '\'' +
                ", productName='" + productName + '\'' +
                ", price='" + price + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
