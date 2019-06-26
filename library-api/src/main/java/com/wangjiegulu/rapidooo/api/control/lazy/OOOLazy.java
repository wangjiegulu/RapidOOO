package com.wangjiegulu.rapidooo.api.control.lazy;

import com.wangjiegulu.rapidooo.api.func.Func0R;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class OOOLazy<T> {
    private Func0R<T> creation;
    private T instance;
    private boolean isSync;

    public OOOLazy(Func0R<T> creation) {
        this(true, creation);
    }

    public OOOLazy(boolean isSync, Func0R<T> creation) {
        this.isSync = isSync;
        this.creation = creation;
    }

    public T get() {
        return isSync ? getInternalSync() : getInternal();
    }

    private T getInternalSync() {
        T result = instance;
        if (null == result) {
            synchronized (this) {
                result = instance;
                if (null == result) {
                    instance = result = creation.call();
                }
            }
        }
        return result;
    }

    private T getInternal() {
        T result = instance;
        if (null == result) {
            instance = result = creation.call();
        }
        return result;
    }


}
