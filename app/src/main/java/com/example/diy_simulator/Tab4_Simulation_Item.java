package com.example.diy_simulator;

public class Tab4_Simulation_Item {

    private String preview_url;
    private String[] url;
    private int width;
    private int height;
    private String category;
    private String name;

    public Tab4_Simulation_Item(String preview_url, String[] url, int width, int height, String category, String name) {
        this.preview_url = preview_url;
        this.url = url;
        this.width = width;
        this.height = height;
        this.category = category;
        this.name = name;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getUrl() {
        return url;
    }

    public void setUrl(String[] url) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
