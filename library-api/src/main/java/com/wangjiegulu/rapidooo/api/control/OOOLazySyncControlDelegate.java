package com.wangjiegulu.rapidooo.api.control;

import com.wangjiegulu.rapidooo.api.control.lazy.OOOLazy;
import com.wangjiegulu.rapidooo.api.func.Func0R;
import com.wangjiegulu.rapidooo.api.func.Func1;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class OOOLazySyncControlDelegate<T> implements OOOControlDelegate<T, OOOLazy<T>> {

    @Override
    public void invoke(final Func0R<T> inputFunc, Func1<OOOLazy<T>> outputFunc) {
        outputFunc.call(new OOOLazy<>(true, new Func0R<T>() {
            @Override
            public T call() {
                return inputFunc.call();
            }
        }));
    }
}
