package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FromFieldConversion {
    private OOOConversion oooConversion;
    private FromElement ownerFromElement;
    private FromField ownerFromField;

    private String fieldName;
    private String targetFieldName;
    private TypeMirror targetType;
    private TypeMirror conversionMethodType;
    private String conversionMethodName;
    private String inverseConversionMethodName;
    private boolean replace;
    private Element targetElement;

    private int conversionMethodNameValidateVariableSize = -1;
    private int inverseConversionMethodNameValidateVariableSize = -1;

    public void setOooConversionAnno(OOOConversion oooConversion) {
        this.oooConversion = oooConversion;
    }

    public void parse() {
        fieldName = oooConversion.fieldName();

        TypeMirror conversionSpecialMethodType = getConversionMethodTypeMirror(oooConversion);
        conversionMethodType = ElementUtil.isSameType(conversionSpecialMethodType, Object.class) ? ownerFromElement.getGeneratorClassEl().asType() : conversionSpecialMethodType;

        conversionMethodName = oooConversion.conversionMethodName();
        targetType = getConversionFromTargetTypeMirror(oooConversion);
        targetElement = MoreTypes.asElement(targetType);
        replace = oooConversion.replace();
        targetFieldName = oooConversion.targetFieldName();
        inverseConversionMethodName = oooConversion.inverseConversionMethodName();

    }

    public void checkConversionMethodValidate() {
        conversionMethodNameValidateVariableSize = checkMethodValidate(conversionMethodType, conversionMethodName,
                targetType,
                ownerFromElement.getTargetClassSimpleName(),
                ownerFromField.getFieldOriginElement().asType()
        );
    }

    public void checkInverseConversionMethodValidate() {
        inverseConversionMethodNameValidateVariableSize = checkMethodValidate(conversionMethodType, inverseConversionMethodName,
                ownerFromField.getFieldOriginElement().asType(),
                ownerFromElement.getTargetClassSimpleName(),
                targetType
        );
    }

    private int checkMethodValidate(TypeMirror conversionMethodType, String conversionMethodName, TypeMirror returnType, String param1Name, TypeMirror param2Type) {
        List<? extends Element> elements = MoreTypes.asElement(conversionMethodType).getEnclosedElements();
        int validateVariableSize = -1;
        for (Element e : elements) {
            if (ElementKind.METHOD == e.getKind()) {
                ExecutableElement methodElement = MoreElements.asExecutable(e);
                // public & static
                if (!MoreElements.hasModifiers(Modifier.STATIC).apply(methodElement)
                        ||
                        !MoreElements.hasModifiers(Modifier.PUBLIC).apply(methodElement)
                        ) {
                    continue;
                }

                if (!e.getSimpleName().toString().equals(conversionMethodName)) {
                    continue;
                }

                if (!ElementUtil.isSameType(methodElement.getReturnType(), returnType)) {
                    continue;
                }
                List<? extends VariableElement> variableElements = methodElement.getParameters();
                int variableElementSize = variableElements.size();
                switch (variableElementSize) {
                    case 1: {
                        if (!ElementUtil.isSameType(variableElements.get(0).asType(), param2Type)) {
                            continue;
                        }
                        validateVariableSize = 1;
                        break;
                    }
                    case 2: {
                        if (!MoreTypes.asTypeElement(variableElements.get(0).asType()).getSimpleName().toString().equals(param1Name)) {
                            continue;
                        }

                        if (!ElementUtil.isSameType(variableElements.get(1).asType(), param2Type)) {
                            continue;
                        }
                        validateVariableSize = 2;
                        break;
                    }
                    default:
                        break;
                }
            }
        }

        if (-1 == validateVariableSize) {
            throw new RuntimeException("No such method [public static "
                    + MoreTypes.asTypeElement(returnType).getSimpleName() + " "
                    + conversionMethodName + "(" + param1Name + ", " + MoreTypes.asTypeElement(param2Type) + ")] in "
                    + MoreTypes.asTypeElement(conversionMethodType).getQualifiedName());
        }
        return validateVariableSize;
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

    public Element getTargetElement() {
        return targetElement;
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

    public FromElement getOwnerFromElement() {
        return ownerFromElement;
    }

    public void setOwnerFromElement(FromElement ownerFromElement) {
        this.ownerFromElement = ownerFromElement;
    }

    public FromField getOwnerFromField() {
        return ownerFromField;
    }

    public void setOwnerFromField(FromField ownerFromField) {
        this.ownerFromField = ownerFromField;
    }

    public int getConversionMethodNameValidateVariableSize() {
        return conversionMethodNameValidateVariableSize;
    }

    public int getInverseConversionMethodNameValidateVariableSize() {
        return inverseConversionMethodNameValidateVariableSize;
    }
}
