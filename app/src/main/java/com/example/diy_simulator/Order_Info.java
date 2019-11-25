package com.example.diy_simulator;


import android.os.Parcel;
import android.os.Parcelable;

public class Order_Info  implements Parcelable {
    private String storename;
    private String delivery_fee;
    private String account_number;
    private String bank_name;

    public Order_Info() {
    }

    public Order_Info(String storename, String delivery_fee) {
        this.storename = storename;
        this.delivery_fee = delivery_fee;
    }

    protected Order_Info(Parcel in) {
        storename = in.readString();
        delivery_fee = in.readString();
    }

    public static final Creator<Order_Info> CREATOR = new Creator<Order_Info>() {
        @Override
        public Order_Info createFromParcel(Parcel in) {
            return new Order_Info(in);
        }

        @Override
        public Order_Info[] newArray(int size) {
            return new Order_Info[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storename);
        dest.writeString(delivery_fee);
    }
}
