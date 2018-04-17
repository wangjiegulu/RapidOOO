package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

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
    //    private TypeMirror targetType;
    private String targetTypeId;
    private TypeName targetType;
    private TypeMirror conversionMethodType;
    private String conversionMethodName;
    private String inverseConversionMethodName;
    private boolean replace;

    private int conversionMethodNameValidateVariableSize = -1;
    private int inverseConversionMethodNameValidateVariableSize = -1;

    public void setOooConversionAnno(OOOConversion oooConversion) {
        this.oooConversion = oooConversion;
    }

    public void parse() {
        targetTypeId = oooConversion.targetTypeId();
        fieldName = oooConversion.fieldName();

        TypeMirror conversionSpecialMethodType = getConversionMethodTypeMirror(oooConversion);
        conversionMethodType = ElementUtil.isSameType(conversionSpecialMethodType, Object.class) ? ownerFromElement.getGeneratorClassEl().asType() : conversionSpecialMethodType;

        conversionMethodName = oooConversion.conversionMethodName();
        targetType = getConversionFromTargetTypeMirror(oooConversion);
        replace = oooConversion.replace();
        targetFieldName = oooConversion.targetFieldName();
        inverseConversionMethodName = oooConversion.inverseConversionMethodName();

    }

    public void checkConversionMethodValidate() {
        conversionMethodNameValidateVariableSize = checkMethodValidate(conversionMethodType, conversionMethodName,
                targetType,
                ownerFromElement.getTargetClassSimpleName(),
                ClassName.get(ownerFromField.getFieldOriginElement().asType())
        );
    }

    public void checkInverseConversionMethodValidate() {
        inverseConversionMethodNameValidateVariableSize = checkMethodValidate(conversionMethodType, inverseConversionMethodName,
                ClassName.get(ownerFromField.getFieldOriginElement().asType()),
                ownerFromElement.getTargetClassSimpleName(),
                targetType
        );
    }

    private int checkMethodValidate(TypeMirror conversionMethodType, String conversionMethodName, TypeName returnType, String param1Name, TypeName param2Type) {
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
                    LogUtil.logger("Method[" + methodElement.getSimpleName() + "] must be `public` and `static`");
                    continue;
                }

                if (!e.getSimpleName().toString().equals(conversionMethodName)) {
                    LogUtil.logger("Method name not matched: " + e.getSimpleName().toString() + " & " + conversionMethodName);
                    continue;
                }

                // ElementUtil.isSameType not work ?
//                if (!ElementUtil.isSameType(methodElement.getReturnType(), returnType)) {
//                    continue;
//                }

                if (!ElementUtil.isSameSimpleName(methodElement.getReturnType(), returnType)) {
                    LogUtil.logger("Method return type not matched");
                    continue;
                }
                List<? extends VariableElement> variableElements = methodElement.getParameters();
                int variableElementSize = variableElements.size();
                switch (variableElementSize) {
                    case 1: {
                        if (!ElementUtil.isSameSimpleName(variableElements.get(0).asType(), param2Type)) {
                            LogUtil.logger("Method variable size 1, and first type not matched");
                            continue;
                        }
                        validateVariableSize = 1;
                        break;
                    }
                    case 2: {
                        if (!MoreTypes.asTypeElement(variableElements.get(0).asType()).getSimpleName().toString().equals(param1Name)) {
                            LogUtil.logger("Method variable size 2, and first type not matched");
                            continue;
                        }

                        if (!ElementUtil.isSameType(variableElements.get(1).asType(), param2Type)) {
                            LogUtil.logger("Method variable size 2, and second type not matched");
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
            throw new RuntimeException("No such method \n[public static "
                    + ((ClassName) returnType).simpleName() + " "
                    + conversionMethodName + "(" + param1Name + ", " + param2Type + ")] \n"
                    + " OR \n[public static "
                    + ((ClassName) returnType).simpleName() + " "
                    + conversionMethodName + "(" + param2Type + ")"
                    + " in "
                    + MoreTypes.asTypeElement(conversionMethodType).getQualifiedName());
        }
        return validateVariableSize;
    }

    public String getFieldName() {
        return fieldName;
    }

    public TypeName getTargetType() {
        return targetType;
    }

    public String getConversionMethodName() {
        return conversionMethodName;
    }

    public void setConversionMethodName(String conversionMethodName) {
        this.conversionMethodName = conversionMethodName;
    }

    public String getTargetTypeId() {
        return targetTypeId;
    }

    private TypeName getConversionFromTargetTypeMirror(OOOConversion oooConversion) {
        // if already id set
        String targetTypeId = oooConversion.targetTypeId();
        FromElement temp;
        if (!AnnoUtil.oooParamIsNotSet(targetTypeId) && null != (temp = ownerFromElement.getFromEntry().getFromElementById(targetTypeId))) {
            return EasyType.bestGuess(temp.getTargetClassFullName());
        }
        // else targetType
        try {
            oooConversion.targetType();
        } catch (MirroredTypeException mte) {
            return TypeName.get(mte.getTypeMirror());
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

    public boolean isTargetTypeId() {
        return null != targetTypeId && !AnnoUtil.oooParamIsNotSet(targetTypeId);
    }
}
