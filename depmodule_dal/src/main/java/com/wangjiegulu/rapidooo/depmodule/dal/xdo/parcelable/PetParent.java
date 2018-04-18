package com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
//@OOOIgnore
public class PetParent implements Parcelable {
    String childName;
    double doublePr;
    Double doubleBox;
    float floatPr;
    Float priceBox;
    Byte byteBox;
    byte bytePr;
    int intPr;
    Integer intBox;
    boolean booleanPr;
    Boolean booleanBox;
    short shortPr;
    Short shortBox;
    Character characterBox;
    char charPr;
    long longPr;
    Long longBox;

    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public double getDoublePr() {
        return doublePr;
    }

    public void setDoublePr(double doublePr) {
        this.doublePr = doublePr;
    }

    public Double getDoubleBox() {
        return doubleBox;
    }

    public void setDoubleBox(Double doubleBox) {
        this.doubleBox = doubleBox;
    }

    public float getFloatPr() {
        return floatPr;
    }

    public void setFloatPr(float floatPr) {
        this.floatPr = floatPr;
    }

    public Float getPriceBox() {
        return priceBox;
    }

    public void setPriceBox(Float priceBox) {
        this.priceBox = priceBox;
    }

    public Byte getByteBox() {
        return byteBox;
    }

    public void setByteBox(Byte byteBox) {
        this.byteBox = byteBox;
    }

    public byte getBytePr() {
        return bytePr;
    }

    public void setBytePr(byte bytePr) {
        this.bytePr = bytePr;
    }

    public int getIntPr() {
        return intPr;
    }

    public void setIntPr(int intPr) {
        this.intPr = intPr;
    }

    public Integer getIntBox() {
        return intBox;
    }

    public void setIntBox(Integer intBox) {
        this.intBox = intBox;
    }

    public boolean isBooleanPr() {
        return booleanPr;
    }

    public void setBooleanPr(boolean booleanPr) {
        this.booleanPr = booleanPr;
    }

    public Boolean getBooleanBox() {
        return booleanBox;
    }

    public void setBooleanBox(Boolean booleanBox) {
        this.booleanBox = booleanBox;
    }

    public short getShortPr() {
        return shortPr;
    }

    public void setShortPr(short shortPr) {
        this.shortPr = shortPr;
    }

    public Short getShortBox() {
        return shortBox;
    }

    public void setShortBox(Short shortBox) {
        this.shortBox = shortBox;
    }

    public Character getCharacterBox() {
        return characterBox;
    }

    public void setCharacterBox(Character characterBox) {
        this.characterBox = characterBox;
    }

    public char getCharPr() {
        return charPr;
    }

    public void setCharPr(char charPr) {
        this.charPr = charPr;
    }

    public long getLongPr() {
        return longPr;
    }

    public void setLongPr(long longPr) {
        this.longPr = longPr;
    }

    public Long getLongBox() {
        return longBox;
    }

    public void setLongBox(Long longBox) {
        this.longBox = longBox;
    }

    public PetParent() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.childName);
        dest.writeDouble(this.doublePr);
        dest.writeValue(this.doubleBox);
        dest.writeFloat(this.floatPr);
        dest.writeValue(this.priceBox);
        dest.writeValue(this.byteBox);
        dest.writeByte(this.bytePr);
        dest.writeInt(this.intPr);
        dest.writeValue(this.intBox);
        dest.writeByte(this.booleanPr ? (byte) 1 : (byte) 0);
        dest.writeValue(this.booleanBox);
        dest.writeInt(this.shortPr);
        dest.writeValue(this.shortBox);
        dest.writeSerializable(this.characterBox);
        dest.writeInt(this.charPr);
        dest.writeLong(this.longPr);
        dest.writeValue(this.longBox);
    }

    protected PetParent(Parcel in) {
        this.childName = in.readString();
        this.doublePr = in.readDouble();
        this.doubleBox = (Double) in.readValue(Double.class.getClassLoader());
        this.floatPr = in.readFloat();
        this.priceBox = (Float) in.readValue(Float.class.getClassLoader());
        this.byteBox = (Byte) in.readValue(Byte.class.getClassLoader());
        this.bytePr = in.readByte();
        this.intPr = in.readInt();
        this.intBox = (Integer) in.readValue(Integer.class.getClassLoader());
        this.booleanPr = in.readByte() != 0;
        this.booleanBox = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.shortPr = (short) in.readInt();
        this.shortBox = (Short) in.readValue(Short.class.getClassLoader());
        this.characterBox = (Character) in.readSerializable();
        this.charPr = (char) in.readInt();
        this.longPr = in.readLong();
        this.longBox = (Long) in.readValue(Long.class.getClassLoader());
    }

}
