package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
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
    public boolean match(OOOTypeEntry typeEntry) {
        TypeName fieldTypeName = typeEntry.getTypeName();
        return fieldTypeName.isPrimitive() && primitiveMap.containsKey(fieldTypeName);
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String statementPrefix, String fieldName, String fieldCode, OOOTypeEntry oooTypeEntry) {
        TypeName fieldTypeName = oooTypeEntry.getTypeName();
        String name = primitiveMap.get(fieldTypeName);
        if (TypeName.BOOLEAN == fieldTypeName) {
            methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.readByte() != 0");
        } else if(TypeName.SHORT == fieldTypeName){
            methodBuilder.addStatement(statementPrefix + fieldCode + " = (short) parcel.readInt()");
        } else if(TypeName.CHAR == fieldTypeName){
            methodBuilder.addStatement(statementPrefix + fieldCode + " = (char) parcel.readInt()");
        } else {
            methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.read" + TextUtil.firstCharUpper(name) + "()");
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String statementPrefix, String fieldName, String fieldCode, OOOTypeEntry oooTypeEntry) {
        TypeName fieldTypeName = oooTypeEntry.getTypeName();
        String name = primitiveMap.get(fieldTypeName);

        if (TypeName.SHORT == fieldTypeName ||
                TypeName.CHAR == fieldTypeName
        ) {
            methodBuilder.addStatement(statementPrefix + "dest.writeInt(" + fieldCode + ")");
        } else if (TypeName.BOOLEAN == fieldTypeName) {
            methodBuilder.addStatement(statementPrefix + "dest.writeByte(" + fieldCode + " ? (byte) 1 : (byte) 0)");
        } else {
            methodBuilder.addStatement(statementPrefix + "dest.write" + TextUtil.firstCharUpper(name) + "(" + fieldCode + ")");
        }
    }
}
