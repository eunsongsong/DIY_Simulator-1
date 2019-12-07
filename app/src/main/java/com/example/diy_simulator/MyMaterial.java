package com.example.diy_simulator;

/**
 * 부자재의 정보를 담는 클래스
 * 이름, 가격, 가로, 세로, 두께, 이미지 URL, 배경 없는 이미지 URL, 재고, 검색 키워드
 * 해당 부자재를 파는 가게 이름, 부자재 카테고리
 */
public class MyMaterial {
    private String material_name = "";
    private String size_width = "";
    private String size_height = "";
    private String image_url = "";

    public MyMaterial(String material_name, String size_width, String size_height) {
        this.material_name = material_name;
        this.size_width = size_width;
        this.size_height = size_height;
    }

    public String getMaterial_name() {
        return material_name;
    }

    public void setMaterial_name(String material_name) {
        this.material_name = material_name;
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
    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}
