package com.wangjiegulu.rapidooo.library.compiler.variables.impl;

import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class OtherObjectVariable implements IOOOVariable {
    private String fieldName;
    private String inputCode;

    public OtherObjectVariable(String fieldName, String inputCode) {
        this.fieldName = fieldName;
        this.inputCode = inputCode;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public String inputCode() {
        return inputCode;
    }
}
