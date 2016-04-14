package com.example.aayush.redirectotp;

/**
 * Created by aayush on 13/4/16.
 */
public class SMSData {
    private String number;
    private String body;

    public SMSData(String number, String body){
        this.number = number;
        this.body = body;
    }
    public String getNumber(){
        return this.number;
    }
    public String getBody(){
        return this.body;
    }
}
