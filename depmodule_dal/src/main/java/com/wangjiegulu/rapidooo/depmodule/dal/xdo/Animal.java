package com.wangjiegulu.rapidooo.depmodule.dal.xdo;

import java.io.Serializable;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 11/04/2018.
 */
public class Animal implements Serializable {
    private int weight;

    public void eat() {
        System.out.println("eat...");
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
