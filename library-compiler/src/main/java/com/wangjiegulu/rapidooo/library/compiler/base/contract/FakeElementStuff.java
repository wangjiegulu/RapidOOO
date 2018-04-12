package com.wangjiegulu.rapidooo.library.compiler.base.contract;

import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FakeElementStuff implements IElementStuff{
    private TypeMirror typeMirror;
    private String simpleName;

    public FakeElementStuff(TypeMirror typeMirror, String simpleName) {
        this.typeMirror = typeMirror;
        this.simpleName = simpleName;
    }

    @Override
    public TypeMirror asType() {
        return typeMirror;
    }

    @Override
    public String getSimpleName() {
        return simpleName;
    }
}
