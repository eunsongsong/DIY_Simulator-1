package com.example.diy_simulator;

/**
 * 부자재의 정보를 담는 클래스
 * 이름, 가격, 가로, 세로, 두께, 이미지 URL, 배경 없는 이미지 URL, 재고, 검색 키워드
 */
public class Material {
    public String material_name = "";
    public String price = "";
    public String size_width = "";
    public String size_height = "";
    public String size_depth = "";
    public String image_url = "";
    public String image_RB_url = "";
    public String stock = "";
    public String keyword = "";
    public String storename = "";

    public Material(String material_name, String price, String size_width, String size_height, String size_depth, String stock, String keyword) {
        this.material_name = material_name;
        this.price = price;
        this.size_width = size_width;
        this.size_height = size_height;
        this.size_depth = size_depth;
        this.stock = stock;
        this.keyword = keyword;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize_width() {
        return size_width;
    }

    public void setSize_width(String size_width) {
        this.size_width = size_width;
    }

    public String getSize_height() {
        return size_height;
    }

    public void setSize_height(String size_height) {
        this.size_height = size_height;
    }

    public String getSize_depth() {
        return size_depth;
    }

    public void setSize_depth(String size_depth) {
        this.size_depth = size_depth;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_RB_url() {
        return image_RB_url;
    }

    public void setImage_RB_url(String image_RB_url) {
        this.image_RB_url = image_RB_url;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
