package com.wangjiegulu.rapidooo.library.compiler.variables.impl;

import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import javax.lang.model.element.VariableElement;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-14.
 */
public class OtherFieldVariable implements IOOOVariable {
    private String fieldName;
    private String inputCode;

    public OtherFieldVariable(VariableElement variableElement, String reference) {
        this.fieldName = variableElement.getSimpleName().toString();
        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(this.fieldName, ElementUtil.getTypeName(variableElement.asType()));
        this.inputCode = reference + "." + getterSetterMethodNames.getGetterMethodName() + "()";
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
