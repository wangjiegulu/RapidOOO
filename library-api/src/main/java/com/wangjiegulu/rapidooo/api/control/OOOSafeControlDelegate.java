package com.wangjiegulu.rapidooo.api.control;

import com.wangjiegulu.rapidooo.api.func.Func0R;
import com.wangjiegulu.rapidooo.api.func.Func1;

import java.lang.ref.WeakReference;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public abstract class OOOSafeControlDelegate<T, R> implements OOOControlDelegate<T, R> {
    @Override
    public final void invoke(Func0R<T> inputFunc, Func1<R> outputFunc) {
        invokeSafe(new WeakReference<>(inputFunc), new WeakReference<>(outputFunc));
    }

    public abstract void invokeSafe(final WeakReference<Func0R<T>> inputFuncWRef,
                            final WeakReference<Func1<R>> outputFuncWRef);
}
