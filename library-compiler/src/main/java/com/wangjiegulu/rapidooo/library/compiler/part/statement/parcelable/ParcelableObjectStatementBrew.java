package com.wangjiegulu.rapidooo.library.compiler.part.statement.parcelable;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IParcelableStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
public class ParcelableObjectStatementBrew implements IParcelableStatementBrew {
    @Override
    public boolean match(ParcelableEntry parcelableEntry) {
        TypeName fieldTypeName = parcelableEntry.fieldType();
        if(ClassName.class == fieldTypeName.getClass()){
            // TODO: 2019-06-21 wangjie 判断不够严谨
            return parcelableEntry.isParcelable();
        }
        return false;
    }

    @Override
    public void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        methodBuilder.addStatement("this." + fieldName + " = parcel.readParcelable(" + ClassName.bestGuess(oooTypeEntry.getTypeName().toString()).simpleName() + ".class.getClassLoader())");
    }

    @Override
    public void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry) {
        methodBuilder.addStatement("dest.writeValue(this." + fieldName + ")");
    }
}
