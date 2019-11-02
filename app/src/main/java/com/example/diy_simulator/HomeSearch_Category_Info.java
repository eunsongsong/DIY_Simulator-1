package com.example.diy_simulator;

public class HomeSearch_Category_Info {
    private String name;
    private String price;
    private String img_url;
    private String storename;

    public HomeSearch_Category_Info(String name, String price, String img_url, String storename) {
        this.name = name;
        this.price = price;
        this.img_url = img_url;
        this.storename = storename;
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

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }
}
