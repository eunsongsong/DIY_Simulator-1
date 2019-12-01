package com.example.diy_simulator;

public class Order_Complete_Info {
    private String storename;
    private String seller_name;
    private String seller_phone;
    private String price;
    private String bank_name;
    private String account_number;

    public Order_Complete_Info(String storename, String seller_name, String seller_phone, String price, String bank_name, String account_number) {
        this.storename = storename;
        this.seller_name = seller_name;
        this.seller_phone = seller_phone;
        this.price = price;
        this.bank_name = bank_name;
        this.account_number = account_number;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getSeller_phone() {
        return seller_phone;
    }

    public void setSeller_phone(String seller_phone) {
        this.seller_phone = seller_phone;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }
}
