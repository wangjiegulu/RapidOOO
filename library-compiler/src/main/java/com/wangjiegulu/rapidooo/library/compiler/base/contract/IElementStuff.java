package com.wangjiegulu.rapidooo.library.compiler.base.contract;

import com.squareup.javapoet.TypeName;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public interface IElementStuff {
    TypeName asType();

    String getSimpleName();
}
