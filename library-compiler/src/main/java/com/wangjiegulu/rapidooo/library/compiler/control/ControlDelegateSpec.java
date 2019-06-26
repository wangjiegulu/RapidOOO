package com.wangjiegulu.rapidooo.library.compiler.control;

import com.squareup.javapoet.TypeName;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public interface ControlDelegateSpec {
    boolean match(TypeName controlDelegateTypeName);
    TypeName convertTargetTypeName(TypeName typeName);
}
