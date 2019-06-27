package com.wangjiegulu.rapidooo.api;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public enum OOOFieldMode {
    ATTACH(1000, "FieldMode-ATTACH"),
    BIND(2000, "FieldMode-BIND"),
    CONVERSION(3000, "FieldMode-CONVERSION"),
    NONE(4000, "");


    private int code;
    private String desc;

    OOOFieldMode(int code, String desc) {
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
