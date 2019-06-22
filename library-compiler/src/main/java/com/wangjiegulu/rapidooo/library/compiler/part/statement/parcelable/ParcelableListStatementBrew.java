package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableListStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(ParcelableEntry parcelableEntry) {
        return parcelableEntry.fieldTypeEntry().isList();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName argumentType = oooTypeEntry.get(0);
        OOOEntry temp = OOOSEntry.queryTypeByName(argumentType.toString());
        // java.util.List<#id__ChatBO>
        if (null != temp || ElementUtil.isSubParcelableType(argumentType.toString())) {
            methodBuilder.addStatement("this." + fieldName + " = parcel.createTypedArrayList($T.CREATOR)", argumentType);
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(argumentType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.createStringArrayList()");
            } else if (TextUtil.equals(argumentType.toString(), RapidOOOConstants.CLASS_NAME_IBINDER)) {
                methodBuilder.addStatement("this." + fieldName + " = parcel.createBinderArrayList()");
            } else {
                methodBuilder.addStatement("parcel.readList(this." + fieldName + ", $T.class.getClassLoader())", argumentType);
                LogUtil.logger("[WARN]Unable parcelable type of list argument: " + argumentType);
            }
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        TypeName argumentType = oooTypeEntry.get(0);
        OOOEntry temp = OOOSEntry.queryTypeByName(argumentType.toString());
        // java.util.List<#id__ChatBO>
        if (null != temp || ElementUtil.isSubParcelableType(argumentType.toString())) {
            methodBuilder.addStatement("dest.writeTypedList(this." + fieldName + ")", argumentType);
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(argumentType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement("dest.writeStringList(this." + fieldName + ")", argumentType);
            } else if (TextUtil.equals(argumentType.toString(), RapidOOOConstants.CLASS_NAME_IBINDER)) {
                methodBuilder.addStatement("dest.writeBinderList(this." + fieldName + ")", argumentType);
            } else {
                LogUtil.logger("[WARN]Unable parcelable type of list argument: " + argumentType);
                methodBuilder.addStatement("dest.writeList(this." + fieldName + ")");
            }
        }
    }
}
