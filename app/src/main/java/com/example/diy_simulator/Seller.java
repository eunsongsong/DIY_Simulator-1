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
    }
}

