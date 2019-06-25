package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOListTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ParcelableListStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(OOOTypeEntry typeEntry) {
        return typeEntry.isList();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String statementPrefix, Object[] statementPrefixTypes, String fieldCode, OOOTypeEntry oooTypeEntry, String fieldName) {
        TypeName argumentType = ((OOOListTypeEntry) oooTypeEntry).getArgumentType();
        // java.util.List<#id__ChatBO>
        if (ElementUtil.isParcelableClassType(argumentType)) {
            methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.createTypedArrayList($T.CREATOR)", TextUtil.addAll(statementPrefixTypes, argumentType));
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(argumentType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.createStringArrayList()", statementPrefixTypes);
            } else if (TextUtil.equals(argumentType.toString(), RapidOOOConstants.CLASS_NAME_IBINDER)) {
                methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.createBinderArrayList()", statementPrefixTypes);
            } else {
                methodBuilder.addStatement("parcel.readList(" + fieldCode + ", $T.class.getClassLoader())", TextUtil.addAll(statementPrefixTypes, argumentType));
                LogUtil.logger("[WARN]Unable parcelable type of list argument: " + argumentType);
            }
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String statementPrefix, Object[] statementPrefixTypes, String fieldCode, OOOTypeEntry oooTypeEntry, String fieldName) {
        TypeName argumentType = ((OOOListTypeEntry) oooTypeEntry).getArgumentType();
        // java.util.List<#id__ChatBO>
        if (ElementUtil.isParcelableClassType(argumentType)) {
            methodBuilder.addStatement(statementPrefix + "dest.writeTypedList(" + fieldCode + ")", TextUtil.addAll(statementPrefixTypes, argumentType));
        } else { // java.util.List<java.lang.String>
            if (TextUtil.equals(argumentType.toString(), String.class.getCanonicalName())) {
                methodBuilder.addStatement(statementPrefix + "dest.writeStringList(" + fieldCode + ")", TextUtil.addAll(statementPrefixTypes, argumentType));
            } else if (TextUtil.equals(argumentType.toString(), RapidOOOConstants.CLASS_NAME_IBINDER)) {
                methodBuilder.addStatement(statementPrefix + "dest.writeBinderList(" + fieldCode + ")", TextUtil.addAll(statementPrefixTypes, argumentType));
            } else {
                LogUtil.logger("[WARN]Unable parcelable type of list argument: " + argumentType);
                methodBuilder.addStatement(statementPrefix + "dest.writeList(" + fieldCode + ")", statementPrefixTypes);
            }
        }
    }
}
