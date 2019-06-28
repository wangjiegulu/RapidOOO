package com.wangjiegulu.rapidooo.library.compiler.control;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.wangjiegulu.rapidooo.api.control.lazy.OOOLazy;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntryFactory;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class LazyControlDelegateSpec extends ControlDelegateSpec {

    public LazyControlDelegateSpec(OOOTypeEntry argTypeEntry) {
        super(argTypeEntry);
    }

    @Override
    protected OOOTypeEntry convertTargetTypeEntry() {
        return OOOTypeEntryFactory.create(ParameterizedTypeName.get(ClassName.get(OOOLazy.class), argTypeEntry.getTypeName()));
    }


}
