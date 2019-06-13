package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class DefaultConstructorMethodPartBrew implements PartBrew{
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        MethodSpec.Builder defaultConstructorMethod = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        result.addMethod(defaultConstructorMethod.build());
    }
}
