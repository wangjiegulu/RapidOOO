package com.wangjiegulu.rapidooo.api;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public enum OOOControlMode {
    ATTACH(1000, "ControlMode-ATTACH"),
    BIND(2000, "ControlMode-BIND"),
    CONVERSION(3000, "ControlMode-CONVERSION");

    private int code;
    private String desc;

    OOOControlMode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
