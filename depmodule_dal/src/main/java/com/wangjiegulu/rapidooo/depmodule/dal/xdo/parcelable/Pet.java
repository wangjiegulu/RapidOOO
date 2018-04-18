package com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
public class Pet extends PetParent implements Parcelable {
    private Long petId;
    private String petName;
    private Long ownerId;

    private boolean isCat;
    private boolean delete;

    private Boolean isDog;
    private Boolean clear;

    private User owner;

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }


    public boolean isCat() {
        return isCat;
    }

    public void setCat(boolean cat) {
        isCat = cat;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public Boolean getDog() {
        return isDog;
    }

    public void setDog(Boolean dog) {
        isDog = dog;
    }

    public Boolean getClear() {
        return clear;
    }

    public void setClear(Boolean clear) {
        this.clear = clear;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Pet() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.petId);
        dest.writeString(this.petName);
        dest.writeValue(this.ownerId);
        dest.writeByte(this.isCat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.delete ? (byte) 1 : (byte) 0);
        dest.writeValue(this.isDog);
        dest.writeValue(this.clear);
        dest.writeParcelable(this.owner, flags);
    }

    protected Pet(Parcel in) {
        super(in);
        this.petId = (Long) in.readValue(Long.class.getClassLoader());
        this.petName = in.readString();
        this.ownerId = (Long) in.readValue(Long.class.getClassLoader());
        this.isCat = in.readByte() != 0;
        this.delete = in.readByte() != 0;
        this.isDog = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.clear = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.owner = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Pet> CREATOR = new Creator<Pet>() {
        @Override
        public Pet createFromParcel(Parcel source) {
            return new Pet(source);
        }

        @Override
        public Pet[] newArray(int size) {
            return new Pet[size];
        }
    };
}
