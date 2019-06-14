package com.wangjiegulu.rapidooo.library.compiler.exception;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class RapidOOOCompileException extends RuntimeException{
    public RapidOOOCompileException() {
    }

    public RapidOOOCompileException(String s) {
        super(s);
    }

    public RapidOOOCompileException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RapidOOOCompileException(Throwable throwable) {
        super(throwable);
    }

    public RapidOOOCompileException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
