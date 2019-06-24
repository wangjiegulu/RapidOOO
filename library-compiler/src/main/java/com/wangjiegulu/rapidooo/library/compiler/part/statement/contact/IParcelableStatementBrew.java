package com.wangjiegulu.rapidooo.library.compiler.part.statement.contact;

import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-17.
 */
public interface IParcelableStatementBrew {
    boolean match(ParcelableEntry parcelableEntry);
    void read(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry);
    void write(MethodSpec.Builder methodBuilder, String fieldName, OOOTypeEntry oooTypeEntry);
}
