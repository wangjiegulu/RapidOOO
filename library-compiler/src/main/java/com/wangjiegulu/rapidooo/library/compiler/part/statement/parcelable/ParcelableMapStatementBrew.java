package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOMapTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntryFactory;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableMapStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(OOOTypeEntry typeEntry) {
        return typeEntry.isMap();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String statementPrefix, Object[] statementPrefixTypes, String fieldCode, OOOTypeEntry oooTypeEntry, String fieldName) {
        OOOMapTypeEntry mapTypeEntry = (OOOMapTypeEntry) oooTypeEntry;

        TypeName keyTypeName = mapTypeEntry.getKeyTypeName();
        TypeName valueTypeName = mapTypeEntry.getValueTypeName();

        String mapSizeFieldName = fieldName + "Size";
        methodBuilder.addCode("int " + mapSizeFieldName + " = parcel.readInt();\n" +
                fieldCode + " = new $T<$T, $T>(" + (mapTypeEntry.isHashMap() ? mapSizeFieldName : "") + ");\n" +
                "for (int i = 0; i < " + mapSizeFieldName + "; i++) {\n", mapTypeEntry.getRawType(), keyTypeName, valueTypeName);
        OOOTypeEntry keyTypeEntry = OOOTypeEntryFactory.create(keyTypeName);
        OOOTypeEntry valueTypeEntry = OOOTypeEntryFactory.create(valueTypeName);

        boolean keyMatched = false;
        boolean valueMatched = false;
        for (IParcelableStatementBrew parcelableStatementBrew : ParcelableStatementUtil.parcelableStatementBrews) {
            if (!keyMatched && parcelableStatementBrew.match(keyTypeEntry)) {
                parcelableStatementBrew.read(methodBuilder, "    $T ", new Object[]{keyTypeEntry.getTypeName()}, "key", keyTypeEntry, "");
                keyMatched = true;
            }
            if (!valueMatched && parcelableStatementBrew.match(valueTypeEntry)) {
                parcelableStatementBrew.read(methodBuilder, "    $T ", new Object[]{valueTypeEntry.getTypeName()}, "value", valueTypeEntry, "");
                valueMatched = true;
            }
            if (keyMatched && valueMatched) {
                break;
            }
        }
        methodBuilder.addCode("    " + fieldCode + ".put(key, value);\n}\n");
    }


    @Override
    public void write(MethodSpec.Builder methodBuilder, String statementPrefix, Object[] statementPrefixTypes, String fieldCode, OOOTypeEntry oooTypeEntry, String fieldName) {
        OOOMapTypeEntry mapTypeEntry = (OOOMapTypeEntry) oooTypeEntry;

        TypeName keyTypeName = mapTypeEntry.getKeyTypeName();
        TypeName valueTypeName = mapTypeEntry.getValueTypeName();

        methodBuilder.addStatement("dest.writeInt(this." + fieldName + ".size())");
        methodBuilder.addCode("for (Map.Entry<$T, $T> entry : this." + fieldName + ".entrySet()) {\n", keyTypeName, valueTypeName);

        OOOTypeEntry keyTypeEntry = OOOTypeEntryFactory.create(keyTypeName);
        OOOTypeEntry valueTypeEntry = OOOTypeEntryFactory.create(valueTypeName);

        boolean keyMatched = false;
        boolean valueMatched = false;
        for (IParcelableStatementBrew parcelableStatementBrew : ParcelableStatementUtil.parcelableStatementBrews) {
            if (!keyMatched && parcelableStatementBrew.match(keyTypeEntry)) {
                parcelableStatementBrew.write(methodBuilder, "    ", RapidOOOConstants.EMPTY_ARRAY, "entry.getKey()", keyTypeEntry, "");
                keyMatched = true;
            }
            if (!valueMatched && parcelableStatementBrew.match(valueTypeEntry)) {
                parcelableStatementBrew.write(methodBuilder, "    ", RapidOOOConstants.EMPTY_ARRAY, "entry.getValue()", valueTypeEntry, "");
                valueMatched = true;
            }
            if (keyMatched && valueMatched) {
                break;
            }
        }
        methodBuilder.addCode("}\n");
    }
}
