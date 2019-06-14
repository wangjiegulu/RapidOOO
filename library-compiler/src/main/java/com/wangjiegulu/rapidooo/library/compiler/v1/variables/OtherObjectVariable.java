package com.wangjiegulu.rapidooo.library.compiler.v1.variables;

import com.wangjiegulu.rapidooo.library.compiler.v1.IOOOVariable;

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
