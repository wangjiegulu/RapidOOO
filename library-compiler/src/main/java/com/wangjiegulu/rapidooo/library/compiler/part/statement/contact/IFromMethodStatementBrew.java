package com.wangjiegulu.rapidooo.library.compiler.part.statement.contact;

import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public interface IFromMethodStatementBrew {
    boolean match(OOOConversionEntry conversionEntry);
    void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec);
}
