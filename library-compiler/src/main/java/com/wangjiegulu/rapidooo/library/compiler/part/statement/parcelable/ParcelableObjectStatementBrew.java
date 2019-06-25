package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
public class ParcelableObjectStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(OOOTypeEntry typeEntry) {
        TypeName fieldTypeName = typeEntry.getTypeName();
        return ClassName.class == fieldTypeName.getClass();
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String statementPrefix, Object[] statementPrefixTypes, String fieldCode, OOOTypeEntry oooTypeEntry, String fieldName) {
        if (ElementUtil.isSameType(oooTypeEntry.getTypeName(), String.class)) {
            methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.readString()", statementPrefixTypes);
        } else {
            methodBuilder.addStatement(statementPrefix + fieldCode + " = parcel.readParcelable(" + ClassName.bestGuess(oooTypeEntry.getTypeName().toString()).simpleName() + ".class.getClassLoader())", statementPrefixTypes);
        }
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String statementPrefix, Object[] statementPrefixTypes, String fieldCode, OOOTypeEntry oooTypeEntry, String fieldName) {
        if (ElementUtil.isSameType(oooTypeEntry.getTypeName(), String.class)) {
            methodBuilder.addStatement(statementPrefix + "dest.writeString(" + fieldCode + ")", statementPrefixTypes);
        } else {
            methodBuilder.addStatement(statementPrefix + "dest.writeValue(" + fieldCode + ")", statementPrefixTypes);
        }

    }
}
