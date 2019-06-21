package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableListStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(ParcelableEntry parcelableEntry) {
        TypeName typeName = parcelableEntry.fieldType();
        String typeNameStr = typeName.toString();
        // TODO: 2019-06-18 wangjie
        return typeName instanceof ParameterizedTypeName && EasyType.isListType(typeNameStr);
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName argumentType = oooTypeEntry.get(0);
        // java.util.List<#id__ChatBO>
        if (oooTypeEntry.hasArgumentRefId()) {
            methodBuilder.addStatement("this." + fieldName + " = parcel.createTypedArrayList($T.CREATOR)", argumentType);
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(argumentType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.createStringArrayList()");
            } else if (TextUtil.equals(argumentType.toString(), "android.os.IBinder")) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.createBinderArrayList()");
            } else {
                LogUtil.logger("[WARN]Unable parcelable type of list argument: " + argumentType);
            }
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName argumentType = oooTypeEntry.get(0);
        // java.util.List<#id__ChatBO>
        if (oooTypeEntry.hasArgumentRefId()) {
            methodBuilder.addStatement("dest.writeTypedList(this." + fieldName + ")", argumentType);
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(argumentType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement("dest.writeStringList(this." + fieldName + ")", argumentType);
            } else if (TextUtil.equals(argumentType.toString(), "android.os.IBinder")) {
                methodBuilder.addStatement("dest.writeBinderList(this." + fieldName + ")", argumentType);
            } else {
                LogUtil.logger("[WARN]Unable parcelable type of list argument: " + argumentType);
            }
        }
    }
}
