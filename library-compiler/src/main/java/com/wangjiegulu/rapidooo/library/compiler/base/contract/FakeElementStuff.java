package com.wangjiegulu.rapidooo.library.compiler.base.contract;

import com.squareup.javapoet.TypeName;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FakeElementStuff implements IElementStuff{
    private TypeName typeName;
    private String simpleName;

    public FakeElementStuff(TypeName typeName, String simpleName) {
        this.typeName = typeName;
        this.simpleName = simpleName;
    }

    @Override
    public TypeName asType() {
        return typeName;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }
}
