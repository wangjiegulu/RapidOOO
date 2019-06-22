package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

import java.util.HashMap;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
public class ParcelablePrimitiveStatementBrew implements IParcelableStatementBrew {
    private HashMap<TypeName, String> primitiveMap = new HashMap<>();

    public ParcelablePrimitiveStatementBrew() {
        primitiveMap.put(TypeName.DOUBLE, "double");
        primitiveMap.put(TypeName.FLOAT, "float");
        primitiveMap.put(TypeName.BYTE, "byte");
        primitiveMap.put(TypeName.INT, "int");
        primitiveMap.put(TypeName.BOOLEAN, "boolean");
        primitiveMap.put(TypeName.SHORT, "short");
        primitiveMap.put(TypeName.CHAR, "char");
        primitiveMap.put(TypeName.LONG, "long");
    }

    @Override
    public boolean match(ParcelableEntry parcelableEntry) {
        TypeName fieldTypeName = parcelableEntry.fieldTypeEntry().getTypeName();
        return fieldTypeName.isPrimitive() && primitiveMap.containsKey(fieldTypeName);
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName fieldTypeName = oooTypeEntry.getTypeName();
        String name = primitiveMap.get(fieldTypeName);
        if (TypeName.BOOLEAN == fieldTypeName) {
            methodBuilder.addStatement("this." + fieldName + " = parcel.readByte() != 0");
        } else if(TypeName.SHORT == fieldTypeName){
            methodBuilder.addStatement("this." + fieldName + " = (short) parcel.readInt()");
        } else if(TypeName.CHAR == fieldTypeName){
            methodBuilder.addStatement("this." + fieldName + " = (char) parcel.readInt()");
        } else {
            methodBuilder.addStatement("this." + fieldName + " = parcel.read" + TextUtil.firstCharUpper(name) + "()");
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName fieldTypeName = oooTypeEntry.getTypeName();
        String name = primitiveMap.get(fieldTypeName);

        if (TypeName.SHORT == fieldTypeName ||
                TypeName.CHAR == fieldTypeName
        ) {
            methodBuilder.addStatement("dest.writeInt(this." + fieldName + ")");
        } else if (TypeName.BOOLEAN == fieldTypeName) {
            methodBuilder.addStatement("dest.writeByte(this." + fieldName + " ? (byte) 1 : (byte) 0)");
        } else {
            methodBuilder.addStatement("dest.write" + TextUtil.firstCharUpper(name) + "(this." + fieldName + ")");
        }
    }
}
