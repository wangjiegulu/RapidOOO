package com.wangjiegulu.rapidooo.library.compiler.util;

import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class LogicUtil {
    public static <T> T checkNullCondition(T t, String s) {
        if (null == t) {
            throw new RapidOOOCompileException(s);
        }
        return t;
    }

    public static <T> List<T> count(Func1R<T, Boolean> func, T...ts){
        List<T> result = new ArrayList<>();
        for(T t : ts){
            if(func.call(t)){
                result.add(t);
            }
        }
        return result;
    }

    public static List<Boolean> countBoolean(Boolean...ts){
        return count(new Func1R<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                return aBoolean;
            }
        }, ts);
    }
}
