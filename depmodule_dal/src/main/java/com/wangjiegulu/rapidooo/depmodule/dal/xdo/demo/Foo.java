package com.wangjiegulu.rapidooo.depmodule.dal.xdo.demo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-22.
 */
public class Foo implements Parcelable {
    private String hello;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.hello);
    }

    public Foo() {
    }

    protected Foo(Parcel in) {
        this.hello = in.readString();
    }

    public static final Parcelable.Creator<Foo> CREATOR = new Parcelable.Creator<Foo>() {
        @Override
        public Foo createFromParcel(Parcel source) {
            return new Foo(source);
        }

        @Override
        public Foo[] newArray(int size) {
            return new Foo[size];
        }
    };
}
