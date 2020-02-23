package com.example.diy_simulator;

public class Tab4_Simulation_Item {

    private String preview_url;
    private String[] url;
    private int width;
    private int height;
    private int depth;
    private String category;
    private String name;
    private boolean isSide;
    private boolean isMy;
    private String unique_number;

    public Tab4_Simulation_Item(){
        this.isMy = true;
    }

    public Tab4_Simulation_Item(String preview_url, String[] url, int width, int height, int depth, String category, String name, boolean isSide, boolean isMy) {
        this.preview_url = preview_url;
        this.url = url;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.category = category;
        this.name = name;
        this.isSide = isSide;
        this.isMy = isMy;
    }


    public boolean isMy() {
        return isMy;
    }

    public void setMy(boolean my) {
        isMy = my;
    }

    public String getUnique_number() {
        return unique_number;
    }

    public void setUnique_number(String unique_number) {
        this.unique_number = unique_number;
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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSide() {
        return isSide;
    }

    public void setSide(boolean side) {
        isSide = side;
    }
}
