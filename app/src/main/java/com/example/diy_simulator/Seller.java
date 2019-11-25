package com.example.diy_simulator;

/**
 * 판매자의 정보를 담는 클래스
 * 이메일, 이름, 전화번호, 주소, 상호명, 주문정보, 부자재정보
 */
public class Seller {

    private String email;
    private String username;
    private String phonenumber;
    private String address;
    private String storename;
    private String material;
    private String delivery_fee;
    private String account_number;
    private String bank_name;
    private String orderinfo;

    //판매자 생성자
    public Seller(String email, String username, String phonenumber, String address, String storename, String delivery_fee, String account_number, String bank_name) {
        this.email = email;
        this.username = username;
        this.phonenumber = phonenumber;
        this.address = address;
        this.storename = storename;
        this.delivery_fee = delivery_fee;
        this.account_number = account_number;
        this.bank_name = bank_name;
        this.material = "";
        this.orderinfo = "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getOrderinfo() {
        return orderinfo;
    }

    public void setOrderinfo(String orderinfo) {
        this.orderinfo = orderinfo;
    }
}

