package com.wangjiegulu.rapidooo.depmodule.dal.xdo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
public class User implements Serializable, Parcelable {
    private Long userId;
    private String username;
    private String nickname;
    private Integer age;
    private Integer gender;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.userId);
        dest.writeString(this.username);
        dest.writeString(this.nickname);
        dest.writeValue(this.age);
        dest.writeValue(this.gender);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.username = in.readString();
        this.nickname = in.readString();
        this.age = (Integer) in.readValue(Integer.class.getClassLoader());
        this.gender = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
