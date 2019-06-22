package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;

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
    public boolean match(ParcelableEntry parcelableEntry) {
        TypeName fieldTypeName = parcelableEntry.fieldTypeEntry().getTypeName();
        if(fieldTypeName.isBoxedPrimitive()){
            TypeName _fieldTypeName = fieldTypeName.unbox();
            return primitiveMap.containsKey(_fieldTypeName);
        }
        return false;
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        String name = primitiveMap.get(oooTypeEntry.getTypeName().unbox());
        methodBuilder.addStatement("this." + fieldName + " = (" + name + ") parcel.readValue(" + name + ".class.getClassLoader())");
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        methodBuilder.addStatement("dest.writeValue(this." + fieldName + ")");
    }
}
