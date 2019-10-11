package com.example.diy_simulator;

/**
 * 판매자의 정보를 담는 클래스
 * 이메일, 이름, 전화번호, 주소, 상호명, 주문정보, 부자재정보
 */
public class Seller {

    public String email = "";
    public String username = "";
    public String phonenumber = "";
    public String address = "";
    public String storename = "";
    public String order = "";
    public String material = "";

    //판매자 생성자
    public Seller(String email, String username, String phonenumber, String address, String storename) {
        this.email = email;
        this.username = username;
        this.phonenumber = phonenumber;
        this.address = address;
        this.storename = storename;
    }
}

