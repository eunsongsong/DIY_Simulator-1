package com.example.diy_simulator;

public class Tab4_Simulation_Item {


    private String data;
    private int width;
    private int height;

    public Tab4_Simulation_Item(String data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
