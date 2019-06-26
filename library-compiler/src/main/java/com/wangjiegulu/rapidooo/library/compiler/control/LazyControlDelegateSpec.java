package com.wangjiegulu.rapidooo.library.compiler.control;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.control.OOOLazyControlDelegate;
import com.wangjiegulu.rapidooo.api.control.OOOLazySyncControlDelegate;
import com.wangjiegulu.rapidooo.api.control.lazy.OOOLazy;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class LazyControlDelegateSpec implements ControlDelegateSpec {
    @Override
    public boolean match(TypeName controlDelegateTypeName) {
        return ElementUtil.isSameType(controlDelegateTypeName, OOOLazyControlDelegate.class)
                ||
                ElementUtil.isSameType(controlDelegateTypeName, OOOLazySyncControlDelegate.class);
    }

    @Override
    public TypeName convertTargetTypeName(TypeName typeName) {
        return ParameterizedTypeName.get(ClassName.get(OOOLazy.class), typeName);
    }
}
