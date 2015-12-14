package com.vip.sdk.api;

/**
 * Created by richard.zhao on 2015-03-20.
 */
public class VipAPIStatus {
    public VipAPIStatus(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public int getCode() {
        return code;
    }

    public void code(int code) {
        this.code = code;
    }

    public String getMessage() {
        return msg;
    }

    public void message(String msg) {
        this.msg = msg;
    }

    private int code;
    private String msg;
}
