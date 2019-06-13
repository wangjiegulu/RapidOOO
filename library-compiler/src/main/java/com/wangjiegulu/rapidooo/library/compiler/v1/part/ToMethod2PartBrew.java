package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class ToMethod2PartBrew implements PartBrew{
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

        // to method 2
        MethodSpec.Builder toFrom2Method = MethodSpec.methodBuilder("to" + oooEntry.getFromSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .returns(oooEntry.getFromTypeName())
                // TODO: 18/04/2018 feature wangjie `from` class use object pool ?
                .addStatement(oooEntry.getFromSimpleName() + " " + fromParamName + " = new " + oooEntry.getFromSimpleName() + "()")
                .addStatement("to" + oooEntry.getFromSimpleName() + "(" + fromParamName + ")")
                .addStatement("return " + fromParamName);

        result.addMethod(toFrom2Method.build());
    }
}
