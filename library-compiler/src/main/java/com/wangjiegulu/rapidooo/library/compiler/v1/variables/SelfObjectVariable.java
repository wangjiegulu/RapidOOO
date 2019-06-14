package com.wangjiegulu.rapidooo.library.compiler.v1.variables;

import com.wangjiegulu.rapidooo.library.compiler.v1.IOOOVariable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class SelfObjectVariable implements IOOOVariable {
    private String fieldName;

    public SelfObjectVariable(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String fieldName() {
        return fieldName;
    }

    @Override
    public String inputCode() {
        return "this";
    }
}
