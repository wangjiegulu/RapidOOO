package com.wangjiegulu.rapidooo.library.compiler.control;

import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public abstract class ControlDelegateSpec {
    protected OOOTypeEntry fullTypeEntry;
    protected OOOTypeEntry argTypeEntry;

    public ControlDelegateSpec(OOOTypeEntry argTypeEntry) {
        this.argTypeEntry = argTypeEntry;
        this.fullTypeEntry = convertTargetTypeEntry();
    }

    protected abstract OOOTypeEntry convertTargetTypeEntry();

    public OOOTypeEntry getFullTypeEntry() {
        return fullTypeEntry;
    }

    public OOOTypeEntry getArgTypeEntry() {
        return argTypeEntry;
    }
}
