package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.util.HashMap;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
public class ParcelableBoxPrimitiveStatementBrew implements IParcelableStatementBrew {
    private HashMap<TypeName, String> primitiveMap = new HashMap<>();

    public ParcelableBoxPrimitiveStatementBrew() {
        primitiveMap.put(TypeName.DOUBLE, "Double");
        primitiveMap.put(TypeName.FLOAT, "Float");
        primitiveMap.put(TypeName.BYTE, "Byte");
        primitiveMap.put(TypeName.INT, "Integer");
        primitiveMap.put(TypeName.BOOLEAN, "Boolean");
        primitiveMap.put(TypeName.SHORT, "Short");
        primitiveMap.put(TypeName.CHAR, "Character");
        primitiveMap.put(TypeName.LONG, "Long");
    }

    @Override
    public boolean match(OOOTypeEntry typeEntry) {
        TypeName fieldTypeName = typeEntry.getTypeName();
        LogUtil.logger("fieldTypeName.isBoxedPrimitive(): " + fieldTypeName.isBoxedPrimitive() + ", " + fieldTypeName);
        if(fieldTypeName.isBoxedPrimitive()){
            TypeName _fieldTypeName = fieldTypeName.unbox();
            return primitiveMap.containsKey(_fieldTypeName);
        }
        return false;
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String statementPrefix, String fieldName, String fieldCode, OOOTypeEntry oooTypeEntry) {
        String name = primitiveMap.get(oooTypeEntry.getTypeName().unbox());
        methodBuilder.addStatement(statementPrefix + fieldCode + " = (" + name + ") parcel.readValue(" + name + ".class.getClassLoader())");
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String statementPrefix, String fieldName, String fieldCode, OOOTypeEntry oooTypeEntry) {
        methodBuilder.addStatement(statementPrefix + "dest.writeValue(" + fieldCode + ")");
    }
}
