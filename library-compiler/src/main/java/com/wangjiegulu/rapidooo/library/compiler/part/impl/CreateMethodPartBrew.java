package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOPoolEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class CreateMethodPartBrew implements PartBrew {
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        String createTargetParam = TextUtil.firstCharLower(oooEntry.getTargetClassSimpleName());

        MethodSpec.Builder createMethod2 = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(oooEntry.getTargetClassType())
                .addParameter(oooEntry.getFromTypeName(), fromParamName);

        if (oooEntry.isPoolUsed()) {
            OOOPoolEntry poolEntry = oooEntry.getPool();
            createMethod2.addStatement(oooEntry.getTargetClassSimpleName() + " " + createTargetParam + " = $T." + poolEntry.getAcquireMethod() + "()", poolEntry.getPoolMethodClassTypeName());
        } else {
            createMethod2.addStatement(oooEntry.getTargetClassSimpleName() + " " + createTargetParam + " = new " + oooEntry.getTargetClassSimpleName() + "()");
        }

        createMethod2.addStatement(createTargetParam + ".from" + oooEntry.getFromSimpleName() + "(" + fromParamName + ")")
                .addStatement("return " + createTargetParam);

        result.addMethod(createMethod2.build());
    }
}
