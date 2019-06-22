package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
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
        OOOEntry temp = OOOSEntry.queryTypeByName(arrayItemType.toString());
        // TODO: 2019-06-21 wangjie
        // #id__ChatBO[]
        if (null != temp) {
            methodBuilder.addStatement("this." + fieldName + " = parcel.createTypedArray($T.CREATOR)", arrayItemType);
        } else { // java.lang.String[]
            if (TextUtil.equals(arrayItemType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.createStringArray()");
            } else if (TextUtil.equals(arrayItemType.toString(), RapidOOOConstants.CLASS_NAME_IBINDER)) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.createBinderArray()");
            } else {
                LogUtil.logger("[WARN]Unable parcelable type of array argument: " + arrayItemType);
            }
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName arrayItemType = oooTypeEntry.getArrayItemTypeName();
        OOOEntry temp = OOOSEntry.queryTypeByName(arrayItemType.toString());
        // java.util.List<#id__ChatBO>
        if (null != temp) {
            methodBuilder.addStatement("dest.writeTypedArray(this." + fieldName + ", flags)", arrayItemType);
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(arrayItemType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement("dest.writeStringArray(this." + fieldName + ")", arrayItemType);
            } else if (TextUtil.equals(arrayItemType.toString(), RapidOOOConstants.CLASS_NAME_IBINDER)) {
                methodBuilder.addStatement("dest.writeBinderArray(this." + fieldName + ")", arrayItemType);
            } else {
                LogUtil.logger("[WARN]Unable parcelable type of array argument: " + arrayItemType);
            }
        }
    }
}
