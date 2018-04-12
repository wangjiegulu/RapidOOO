package com.wangjiegulu.rapidooo.library.compiler.objs;

import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FromFieldConversion {
    private String fieldName;
    private TypeMirror targetType;
    private String conversionMethodName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public TypeMirror getTargetType() {
        return targetType;
    }

    public void setTargetType(TypeMirror targetType) {
        this.targetType = targetType;
    }

    public String getConversionMethodName() {
        return conversionMethodName;
    }

    public void setConversionMethodName(String conversionMethodName) {
        this.conversionMethodName = conversionMethodName;
    }
}
