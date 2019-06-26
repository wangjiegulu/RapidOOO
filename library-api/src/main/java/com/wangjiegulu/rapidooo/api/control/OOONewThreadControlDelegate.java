package com.wangjiegulu.rapidooo.api.control;

import com.wangjiegulu.rapidooo.api.func.Func0R;
import com.wangjiegulu.rapidooo.api.func.Func1;

import java.lang.ref.WeakReference;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class OOONewThreadControlDelegate<T> implements OOOControlDelegate<T, T> {
    @Override
    public void invoke(final Func0R<T> inputFunc, final Func1<T> outputFunc) {
        invokeSafe(new WeakReference<>(inputFunc), new WeakReference<>(outputFunc));
    }

    private void invokeSafe(final WeakReference<Func0R<T>> inputFuncWRef,
                            final WeakReference<Func1<T>> outputFuncWRef) {
        // TODO: 2019-06-26 wangjie
        new Thread(new Runnable() {
            @Override
            public void run() {
                Func0R<T> inputFunc = inputFuncWRef.get();
                Func1<T> outputFunc = outputFuncWRef.get();
                if (null != inputFunc && null != outputFunc) {
                    outputFunc.call(inputFunc.call());
                }
            }
        }).start();
    }
}
