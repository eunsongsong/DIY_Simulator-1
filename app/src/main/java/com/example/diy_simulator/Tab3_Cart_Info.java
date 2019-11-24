package com.example.diy_simulator;

import java.io.Serializable;
import java.util.List;

public class Tab3_Cart_Info implements Serializable {
    private String storename;
    private String delivery_fee;
    private List<Tab3_Cart_In_Item_Info> in_items;
    private boolean isAnySelected;

    public Tab3_Cart_Info() {
    }

    public Tab3_Cart_Info(String storename, String delivery_fee, List<Tab3_Cart_In_Item_Info> in_items) {
        this.storename = storename;
        this.delivery_fee = delivery_fee;
        this.in_items = in_items;
        this.isAnySelected = true;
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

    public List<Tab3_Cart_In_Item_Info> getIn_items() {
        return in_items;
    }

    public void setIn_items(List<Tab3_Cart_In_Item_Info> in_items) {
        this.in_items = in_items;
    }

    public boolean getAnySelected() {
        return isAnySelected;
    }

    public void setAnySelected(boolean anySelected) {
        isAnySelected = anySelected;
    }


}
