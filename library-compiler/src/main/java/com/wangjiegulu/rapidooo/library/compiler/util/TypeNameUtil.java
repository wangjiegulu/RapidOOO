package com.wangjiegulu.rapidooo.library.compiler.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 3/16/16.
 */
public class TypeNameUtil {
    public static String getParcelablePrimitiveReadStatement(TypeName typeName) {
        if (typeName == TypeName.DOUBLE) {
            return "parcel.readDouble()";
        } else if (typeName == TypeName.FLOAT) {
            return "parcel.readFloat()";
        } else if (typeName == TypeName.BYTE) {
            return "parcel.readByte()";
        } else if (typeName == TypeName.INT) {
            return "parcel.readInt()";
        } else if (typeName == TypeName.BOOLEAN) {
            return "parcel.readByte() != 0";
        } else if (typeName == TypeName.SHORT) {
            return "(short) parcel.readInt()";
        } else if (typeName == TypeName.CHAR) {
            return "(char) parcel.readInt()";
        } else if (typeName == TypeName.LONG) {
            return "parcel.readLong()";
        }
        return null;
    }

    public static String getParcelableBoxPrimitiveReadStatement(TypeName typeName) {
        TypeName typeNameTemp = typeName.unbox();
        if (typeNameTemp == TypeName.DOUBLE) {
            return "(Double) parcel.readValue(Double.class.getClassLoader())";
        } else if (typeNameTemp == TypeName.FLOAT) {
            return "(Float) parcel.readValue(Float.class.getClassLoader())";
        } else if (typeNameTemp == TypeName.BYTE) {
            return "(Byte) parcel.readValue(Byte.class.getClassLoader())";
        } else if (typeNameTemp == TypeName.INT) {
            return "(Integer) parcel.readValue(Integer.class.getClassLoader())";
        } else if (typeNameTemp == TypeName.BOOLEAN) {
            return "(Boolean) parcel.readValue(Boolean.class.getClassLoader())";
        } else if (typeNameTemp == TypeName.SHORT) {
            return "(Short) parcel.readValue(Short.class.getClassLoader())";
        } else if (typeNameTemp == TypeName.CHAR) {
            return "(Character) parcel.readSerializable()";
        } else if (typeNameTemp == TypeName.LONG) {
            return "(Long) parcel.readValue(Long.class.getClassLoader())";
        }
        return null;
    }

    public static String getParcelableOtherReadStatement(TypeName typeName) {
        if (ElementUtil.isSameSimpleName(String.class, typeName)) {
            return "parcel.readString()";
        }

        // TODO: 17/04/2018 wangjie other type, such as Serializable
        return "parcel.readParcelable(" + ClassName.bestGuess(typeName.toString()).simpleName() + ".class.getClassLoader())";
    }


    public static String getParcelablePrimitiveWriteStatement(TypeName typeName, String fieldName) {
        if (typeName == TypeName.DOUBLE) {
            return "dest.writeDouble(this." + fieldName + ")";
        } else if (typeName == TypeName.FLOAT) {
            return "dest.writeFloat(this." + fieldName + ")";
        } else if (typeName == TypeName.BYTE) {
            return "dest.writeByte(this." + fieldName + ")";
        } else if (typeName == TypeName.INT) {
            return "dest.writeInt(this." + fieldName + ")";
        } else if (typeName == TypeName.BOOLEAN) {
            return "dest.writeByte(this." + fieldName + " ? (byte) 1 : (byte) 0);";
        } else if (typeName == TypeName.SHORT) {
            return "dest.writeInt(this." + fieldName + ")";
        } else if (typeName == TypeName.CHAR) {
            return "dest.writeInt(this." + fieldName + ")";
        } else if (typeName == TypeName.LONG) {
            return "dest.writeLong(this." + fieldName + ")";
        }
        return null;
    }


    public static String getParcelableOtherWriteStatement(TypeName typeName, String fieldName) {
        if (ElementUtil.isSameSimpleName(String.class, typeName)) {
            return "dest.writeString(this." + fieldName + ")";
        }
        // TODO: 17/04/2018 wangjie other type, such as Serializable
        return "dest.writeParcelable(this." + fieldName + ", flags)";
    }
}
