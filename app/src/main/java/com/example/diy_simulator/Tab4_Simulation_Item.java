package com.example.diy_simulator;

public class Tab4_Simulation_Item {


    private String url;
    private int width;
    private int height;
    private String category;

    public Tab4_Simulation_Item(String url, int width, int height, String category) {
        this.url = url;
        this.width = width;
        this.height = height;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
