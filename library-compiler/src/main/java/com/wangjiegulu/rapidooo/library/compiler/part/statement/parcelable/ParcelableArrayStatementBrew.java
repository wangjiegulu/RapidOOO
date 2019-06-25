package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOArrayTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableArrayStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(OOOTypeEntry typeEntry) {
        return typeEntry.isArray();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String statementPrefix, String fieldName, String fieldCode, OOOTypeEntry oooTypeEntry) {
        TypeName arrayItemType = ((OOOArrayTypeEntry) oooTypeEntry).getArrayItemTypeName();
        // TODO: 2019-06-21 wangjie
        // #id__ChatBO[]
        if (ElementUtil.isParcelableType(arrayItemType)) {
            methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.createTypedArray($T.CREATOR)", arrayItemType);
        } else { // java.lang.String[]
            if (arrayItemType.isPrimitive()) {
                methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.create" + TextUtil.firstCharUpper(arrayItemType.toString()) + "Array()");
            } else {
                methodBuilder.addStatement(statementPrefix + fieldCode + " = ($T) parcel.readArray($T.class.getClassLoader())", oooTypeEntry.getTypeName(), oooTypeEntry.getTypeName());
            }
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String statementPrefix, String fieldName, String fieldCode, OOOTypeEntry oooTypeEntry) {
        TypeName arrayItemType = ((OOOArrayTypeEntry) oooTypeEntry).getArrayItemTypeName();
        // java.util.List<#id__ChatBO>
        if (ElementUtil.isParcelableType(arrayItemType)) {
            methodBuilder.addStatement(statementPrefix + "dest.writeTypedArray(" +  fieldCode + ", flags)", arrayItemType);
        } else { // java.util.List<java.lang.String>
            if (arrayItemType.isPrimitive()) {
                methodBuilder.addStatement(statementPrefix + "dest.write" + TextUtil.firstCharUpper(arrayItemType.toString()) + "Array(" + fieldCode + ")");
            } else {
                methodBuilder.addStatement(statementPrefix + "dest.writeArray(" + fieldCode + ")");
            }
        }
    }
}
