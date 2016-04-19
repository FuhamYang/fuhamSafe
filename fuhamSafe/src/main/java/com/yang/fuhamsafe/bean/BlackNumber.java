package com.yang.fuhamsafe.bean;

/**
 * Created by fuhamyang on 2015/12/12.
 */
public class BlackNumber {

    String number;
    String type;
    public BlackNumber() {

    }
    public BlackNumber(String number, String type) {
        this.type = type;
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {

        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
