package com.wangjiegulu.rapidooo.library.compiler.control;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.control.OOOControlDelegate;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class NormalControlDelegateSpec implements ControlDelegateSpec{
    @Override
    public boolean match(TypeName controlDelegateTypeName) {
        return !ElementUtil.isSameType(controlDelegateTypeName, OOOControlDelegate.class);
    }

    @Override
    public TypeName convertTargetTypeName(TypeName typeName) {
        return typeName;
    }
}
