package com.example.diy_simulator;

public class Tab3_MyStore_Info {
    private String name;
    private String price;
    private String img_data;

    public Tab3_MyStore_Info(String name, String price, String img_data) {
        this.name = name;
        this.price = price;
        this.img_data = img_data;
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

    public String getImg_data() {
        return img_data;
    }

    public void setImg_url(String img_url) {
        this.img_data = img_data;
    }
}


