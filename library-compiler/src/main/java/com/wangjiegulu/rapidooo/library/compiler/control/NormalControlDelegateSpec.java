package com.wangjiegulu.rapidooo.library.compiler.control;

import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class NormalControlDelegateSpec extends ControlDelegateSpec{

    public NormalControlDelegateSpec(OOOTypeEntry argTypeEntry) {
        super(argTypeEntry);
    }

    @Override
    protected OOOTypeEntry convertTargetTypeEntry() {
        return argTypeEntry;
    }


}
