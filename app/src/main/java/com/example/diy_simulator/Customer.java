package com.example.diy_simulator;

/**
 * 구매자의 정보를 담는 클래스
 * 이메일, 이름, 전화번호, 주소
 */
public class Customer {

    public String email = "";
    public String username = "";
    public String phonenumber = "";
    public String address = "";

    //구매자 생성자
    public Customer(String email, String username, String phonenumber, String address) {
        this.email = email;
        this.username = username;
        this.phonenumber = phonenumber;
        this.address = address;
    }
}


