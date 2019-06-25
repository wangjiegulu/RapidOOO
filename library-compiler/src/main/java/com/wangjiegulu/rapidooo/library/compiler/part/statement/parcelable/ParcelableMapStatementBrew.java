package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOMapTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableMapStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(ParcelableEntry parcelableEntry) {
        return parcelableEntry.fieldTypeEntry().isMap();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
//        int scoreMapSize = in.readInt();
//        this.scoreMap = new HashMap<String, Integer>(scoreMapSize);
//        for (int i = 0; i < scoreMapSize; i++) {
//            String key = in.readString();
//            Integer value = (Integer) in.readValue(Integer.class.getClassLoader());
//            this.scoreMap.put(key, value);
//        }

        // TODO: 2019-06-25 wangjie
        OOOMapTypeEntry mapTypeEntry = (OOOMapTypeEntry) oooTypeEntry;

        String mapSizeFieldName = fieldName + "Size";
        methodBuilder.addCode("int " + mapSizeFieldName + " = parcel.readInt();\n" +
                        "this." + fieldName + " = new HashMap<>(" + mapSizeFieldName + ");\n" +
                        "for (int i = 0; i < " + mapSizeFieldName + "; i++) {\n");
        methodBuilder.addCode("");
        methodBuilder.addCode("    this." + fieldName + ".put(key, value);\n}\n");

    }


    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {

    }
}
