package com.example.diy_simulator;

public class HomeSearch_Category_Info {
    private String name;
    private String price;
    private String preview_img_url;
    private String[] img_url;
    private String width;
    private String height;
    private String depth;
    private String keyword;
    private String stock;
    private String storename;
    private String unique_number;


    public HomeSearch_Category_Info(String name, String price, String preview_img_url, String[] img_url,
                                    String width, String height, String depth, String keyword, String stock, String storename, String unique_number) {
        this.name = name;
        this.price = price;
        this.preview_img_url = preview_img_url;
        this.img_url = img_url;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.keyword = keyword;
        this.stock = stock;
        this.storename = storename;
        this.unique_number = unique_number;
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

    public String getPreview_img_url() {
        return preview_img_url;
    }

    public void setPreview_img_url(String preview_img_url) {
        this.preview_img_url = preview_img_url;
    }

    public String[] getImg_url() {
        return img_url;
    }

    public void setImg_url(String[] img_url) {
        this.img_url = img_url;
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
}
