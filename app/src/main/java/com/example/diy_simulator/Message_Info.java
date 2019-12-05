package com.example.diy_simulator;

public class Message_Info {
    String who;
    String msg_content;
    String time;

    public Message_Info(String who,String msg_content, String time) {
        this.who = who;
        this.msg_content = msg_content;
        this.time = time;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
