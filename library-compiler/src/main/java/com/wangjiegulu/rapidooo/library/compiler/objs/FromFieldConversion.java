package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreTypes;

import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FromFieldConversion {
    private OOOConversion oooConversion;
    private String fieldName;
    private String targetFieldName;
    private TypeMirror targetType;
    private TypeMirror conversionMethodType;
    private String conversionMethodName;
    private String inverseConversionMethodName;
    private boolean replace;
    private Element element;

    public void setOooConversionAnno(OOOConversion oooConversion) {
        this.oooConversion = oooConversion;
        parse();
    }

    private void parse() {
        fieldName = oooConversion.fieldName();
        conversionMethodType = getConversionMethodTypeMirror(oooConversion);
        conversionMethodName = oooConversion.conversionMethodName();
        targetType = getConversionFromTargetTypeMirror(oooConversion);
        element = MoreTypes.asElement(targetType);
        replace = oooConversion.replace();
        targetFieldName = oooConversion.targetFieldName();
        inverseConversionMethodName = oooConversion.inverseConversionMethodName();
    }

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

    private static TypeMirror getConversionFromTargetTypeMirror(OOOConversion oooConversion) {
        try {
            oooConversion.targetType();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        throw new RuntimeException("getConversionFromTargetTypeMirror error");
    }

    private static TypeMirror getConversionMethodTypeMirror(OOOConversion oooConversion) {
        try {
            oooConversion.conversionMethodClass();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        throw new RuntimeException("getConversionMethodTypeMirror error");
    }

    public Element getElement() {
        return element;
    }

    public boolean isReplace() {
        return replace;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public String getInverseConversionMethodName() {
        return inverseConversionMethodName;
    }

    public TypeMirror getConversionMethodType() {
        return conversionMethodType;
    }

    public TypeMirror getConversionMethodType(TypeMirror defaultValue) {
        return ElementUtil.isSameType(conversionMethodType, Object.class) ? defaultValue : conversionMethodType;
    }
}
