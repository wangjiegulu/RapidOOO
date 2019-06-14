package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOPoolEntry;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class PoolPartBrew implements PartBrew{
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        if (oooEntry.isPoolUsed()) {
            // release method for object pool
            OOOPoolEntry poolEntry = oooEntry.getPool();
            MethodSpec.Builder releaseMethodBuilder = MethodSpec.methodBuilder("release")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addStatement("$T." + poolEntry.getReleaseMethod() + "(this)", poolEntry.getPoolMethodClassType());

            result.addMethod(releaseMethodBuilder.build());
        }
    }
}
