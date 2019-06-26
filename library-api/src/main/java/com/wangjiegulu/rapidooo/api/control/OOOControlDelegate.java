package com.wangjiegulu.rapidooo.api.control;

import com.wangjiegulu.rapidooo.api.func.Func0R;
import com.wangjiegulu.rapidooo.api.func.Func1;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public interface OOOControlDelegate<T, R> {
    void invoke(Func0R<T> inputFunc, Func1<R> outputFunc);
}
