package com.example.diy_simulator;

import java.io.Serializable;
import java.util.List;

public class Order_Info implements Serializable {
    private String storename;
    private String delivery_fee;
    private String order_price;
    private String account_number;
    private String bank_name;
    private String delivery_recipient;
    private String delivery_destination;
    private String delivery_phone;
    private String delivery_memo;
    private String order_state;
    private List<Order_Product_Info> order_items;
    private String order_number;

    public Order_Info(){
    }

    public Order_Info(String storename, String delivery_fee, String order_price, String account_number, String bank_name,
                      String delivery_recipient, String delivery_destination, String delivery_phone, String delivery_memo, String order_state, List<Order_Product_Info> order_items) {
        this.storename = storename;
        this.delivery_fee = delivery_fee;
        this.order_price = order_price;
        this.account_number = account_number;
        this.bank_name = bank_name;
        this.delivery_recipient = delivery_recipient;
        this.delivery_destination = delivery_destination;
        this.delivery_phone = delivery_phone;
        this.delivery_memo = delivery_memo;
        this.order_state = order_state;
        this.order_items = order_items;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String storename) {
        this.storename = storename;
    }

    public String getDelivery_fee() {
        return delivery_fee;
    }

    public void setDelivery_fee(String delivery_fee) {
        this.delivery_fee = delivery_fee;
    }

    public String getOrder_price() {
        return order_price;
    }

    public void setOrder_price(String order_price) {
        this.order_price = order_price;
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

    public String getDelivery_recipient() {
        return delivery_recipient;
    }

    public void setDelivery_recipient(String delivery_recipient) {
        this.delivery_recipient = delivery_recipient;
    }

    public String getDelivery_destination() {
        return delivery_destination;
    }

    public void setDelivery_destination(String delivery_destination) {
        this.delivery_destination = delivery_destination;
    }

    public String getDelivery_phone() {
        return delivery_phone;
    }

    public void setDelivery_phone(String delivery_phone) {
        this.delivery_phone = delivery_phone;
    }

    public String getDelivery_memo() {
        return delivery_memo;
    }

    public void setDelivery_memo(String delivery_memo) {
        this.delivery_memo = delivery_memo;
    }

    public String getOrder_state() {
        return order_state;
    }

    public void setOrder_state(String order_state) {
        this.order_state = order_state;
    }

    public List<Order_Product_Info> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<Order_Product_Info> order_items) {
        this.order_items = order_items;
    }

    public String getOrder_number() {
        return order_number;
    }

    public void setOrder_number(String order_number) {
        this.order_number = order_number;
    }
}
