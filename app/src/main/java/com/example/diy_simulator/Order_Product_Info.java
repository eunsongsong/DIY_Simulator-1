package com.example.diy_simulator;

public class Order_Product_Info {
    private String product_name;
    private String product_price;
    private String product_amount;
    private String product_url;

    public Order_Product_Info(String product_name, String product_price, String product_amount, String product_url) {
        this.product_name = product_name;
        this.product_price = product_price;
        this.product_amount = product_amount;
        this.product_url = product_url;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }

    public String getProduct_amount() {
        return product_amount;
    }

    public void setProduct_amount(String product_amount) {
        this.product_amount = product_amount;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }
}
