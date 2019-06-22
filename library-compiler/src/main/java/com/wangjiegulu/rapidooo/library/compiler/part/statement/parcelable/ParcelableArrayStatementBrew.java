package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableArrayStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(ParcelableEntry parcelableEntry) {
        return parcelableEntry.fieldTypeEntry().isArray();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName arrayItemType = oooTypeEntry.getArrayItemTypeName();
        // TODO: 2019-06-21 wangjie
        // #id__ChatBO[]
        if (ElementUtil.isParcelableType(arrayItemType)) {
            methodBuilder.addStatement("this." + fieldName + " = parcel.createTypedArray($T.CREATOR)", arrayItemType);
        } else { // java.lang.String[]
            if (arrayItemType.isPrimitive()) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.create" + TextUtil.firstCharUpper(arrayItemType.toString()) + "Array()");
            } else {
                methodBuilder.addStatement("this." + fieldName + " = ($T) parcel.readArray($T.class.getClassLoader())", oooTypeEntry.getTypeName(), oooTypeEntry.getTypeName());
            }

        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName arrayItemType = oooTypeEntry.getArrayItemTypeName();
        // java.util.List<#id__ChatBO>
        if (ElementUtil.isParcelableType(arrayItemType)) {
            methodBuilder.addStatement("dest.writeTypedArray(this." + fieldName + ", flags)", arrayItemType);
        } else { // java.util.List<java.lang.String>
            if (arrayItemType.isPrimitive()) {
                methodBuilder.addStatement("dest.write" + TextUtil.firstCharUpper(arrayItemType.toString()) + "Array(this." + fieldName + ")");
            } else {
                methodBuilder.addStatement("dest.writeArray(this." + fieldName + ")");
            }
        }
    }
}
