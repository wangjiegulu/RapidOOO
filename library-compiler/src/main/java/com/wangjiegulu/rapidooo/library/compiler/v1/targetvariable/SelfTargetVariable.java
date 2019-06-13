package com.wangjiegulu.rapidooo.library.compiler.v1.targetvariable;

import com.wangjiegulu.rapidooo.library.compiler.v1.IOOOTargetVariable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class SelfTargetVariable implements IOOOTargetVariable {
    private String fieldName;

    public SelfTargetVariable(String fieldName) {
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
