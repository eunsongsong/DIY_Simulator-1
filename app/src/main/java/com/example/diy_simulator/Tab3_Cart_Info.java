package com.example.diy_simulator;

public class Tab3_Cart_Info {
    private String name;
    private String price;
    private String preview_img_data;
    private String[] img_data;
    private String width;
    private String height;
    private String depth;
    private String keyword;
    private String stock;
    private String storename;
    private String unique_number;
    private int amount;

    public Tab3_Cart_Info(String name, String price, String preview_img_data, String[] img_data,
                          String width, String height, String depth, String keyword, String stock, String storename, String unique_number, int amount) {
        this.name = name;
        this.price = price;
        this.preview_img_data = preview_img_data;
        this.img_data = img_data;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.keyword = keyword;
        this.stock = stock;
        this.storename = storename;
        this.unique_number = unique_number;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPreview_img_data() {
        return preview_img_data;
    }

    public void setPreview_img_data(String preview_img_data) {
        this.preview_img_data = preview_img_data;
    }

    public String[] getImg_data() {
        return img_data;
    }

    public void setImg_data(String[] img_data) {
        this.img_data = img_data;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getUnique_number() {
        return unique_number;
    }

    public void setUnique_number(String unique_number) {
        this.unique_number = unique_number;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
