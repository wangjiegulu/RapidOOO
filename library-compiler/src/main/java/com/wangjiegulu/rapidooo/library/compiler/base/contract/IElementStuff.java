package com.wangjiegulu.rapidooo.library.compiler.base.contract;

import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public interface IElementStuff {
    TypeMirror asType();

    String getSimpleName();
}
