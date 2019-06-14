package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogicUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func0R;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;
import com.wangjiegulu.rapidooo.library.compiler.variables.impl.OtherFieldVariable;
import com.wangjiegulu.rapidooo.library.compiler.variables.impl.OtherObjectVariable;
import com.wangjiegulu.rapidooo.library.compiler.variables.impl.SelfObjectVariable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOConversionEntry implements IOOOVariable {
    private OOOEntry oooEntry;
    private OOOConversion oooConversion;

    private String targetFieldName;
    private TypeName targetFieldType;
    private String targetFieldTypeId;

    // attach
    private String attachFieldName;
//    private String attachFieldType; // as some as targetFieldType

    // bind
    private TypeName bindMethodClassType;
    private TypeMirror bindMethodClass;
    private String bindMethodName;
    private String inverseBindMethodName;
    private HashMap<String, IOOOVariable> bindTargetParamFields = new LinkedHashMap<>();
    private HashMap<String, IOOOVariable> inverseBindTargetParamFields = new LinkedHashMap<>();

    // conversion
    private TypeName conversionMethodClassType;
    private TypeMirror conversionMethodClass;
    private String conversionMethodName;
    private String inverseConversionMethodName;
    private HashMap<String, IOOOVariable> conversionTargetParamFields = new LinkedHashMap<>();
    private HashMap<String, IOOOVariable> inverseConversionTargetParamFields = new LinkedHashMap<>();

    private OOOControlMode controlMode;

    public OOOConversionEntry(OOOEntry oooEntry, final OOOConversion oooConversion) {
        this.oooEntry = oooEntry;
        this.oooConversion = oooConversion;

        targetFieldName = oooConversion.targetFieldName();
        targetFieldType = ElementUtil.getTypeName(AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return oooConversion.targetFieldType();
            }
        }));
        targetFieldTypeId = oooConversion.targetFieldTypeId();

        // attach mode
        attachFieldName = oooConversion.attachFieldName();


        // bind mode
        bindMethodClass = AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return oooConversion.bindMethodClass();
            }
        });
        bindMethodClassType = ElementUtil.getTypeName(bindMethodClass);
        if (ElementUtil.isSameType(bindMethodClassType, TypeName.get(Object.class))) { // 如果没有配置，则默认是 Generator 类
            bindMethodClassType = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType();
            bindMethodClass = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassEl().asType();
        }
        bindMethodName = oooConversion.bindMethodName();
        inverseBindMethodName = oooConversion.inverseBindMethodName();


        // conversion mode
        conversionMethodClass = AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return oooConversion.conversionMethodClass();
            }
        });
        conversionMethodClassType = ElementUtil.getTypeName(conversionMethodClass);
        if (ElementUtil.isSameType(conversionMethodClassType, TypeName.get(Object.class))) { // 如果没有配置，则默认是 Generator 类
            conversionMethodClassType = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType();
            conversionMethodClass = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassEl().asType();
        }
        conversionMethodName = oooConversion.conversionMethodName();
        inverseConversionMethodName = oooConversion.inverseConversionMethodName();

        init();

    }

    public OOOConversionEntry prepare() {
        if (!AnnoUtil.oooParamIsNotSet(targetFieldTypeId)) { // 设置了 type id，则从缓存中查询
            targetFieldType = oooEntry.getOoosEntry().queryTypeIds(targetFieldTypeId).getTargetClassType();
        }
        return this;
    }

    public void parse() {
        parseControlMode();
    }

    private void init() {

    }

    /**
     * 解析 control mode 的类型，并做好校验工作
     */
    private void parseControlMode() {
        boolean attachSet = !AnnoUtil.oooParamIsNotSet(attachFieldName);
        boolean bindSet = !AnnoUtil.oooParamIsNotSet(bindMethodName) || !AnnoUtil.oooParamIsNotSet(inverseBindMethodName);
        boolean conversionSet = !AnnoUtil.oooParamIsNotSet(conversionMethodName) || !AnnoUtil.oooParamIsNotSet(inverseConversionMethodName);
        if (LogicUtil.countBoolean(attachSet, bindSet, conversionSet).size() > 1) {
            throw new RapidOOOCompileException("Can not be set ATTACH or BIND or CONVERSION at the same time.");
        }
        if (bindSet) {
            controlMode = OOOControlMode.BIND;
            parseBindMode();
        } else if (conversionSet) {
            controlMode = OOOControlMode.CONVERSION;
            parseConversionMode();
        } else {
            controlMode = OOOControlMode.ATTACH;
            if (AnnoUtil.oooParamIsNotSet(attachFieldName)) {
                attachFieldName = targetFieldName;
            }
            parseAttachMode();
        }

    }

    private void parseAttachMode() {
        // TODO: 2019-06-14 wangjie check if attachFieldName in fromClassType?

    }

    private void parseBindMode() {
        if (isBindMethodSet()) {
            // 检查 bind method 是否存在
            ExecutableElement method = findPublicStaticMethodInClass(bindMethodClass, bindMethodName, targetFieldType);
            if (null == method) {
                throw new RapidOOOCompileException("Method[public static " + targetFieldType.toString() + " " + bindMethodName + "(...)] not found \nin " + bindMethodClass.toString() + " class.");
            }
            // 检查 bind method 方法中的所有参数是否存在在 OOO 中
            bindTargetParamFields = findBindMethodVariables(method);
        }
        if (isInverseBindMethodSet()) {
            // 检查 bind method 是否存在
            ExecutableElement method = findPublicStaticMethodInClass(bindMethodClass, inverseBindMethodName, ElementUtil.getTypeName(void.class));
            if (null == method) {
                throw new RapidOOOCompileException("Method[public static void " + inverseBindMethodName + "(...)] not found \nin " + bindMethodClass.toString() + " class.");
            }
            // 检查 bind method 方法中的所有参数是否存在在 OOO 中
            inverseBindTargetParamFields = findBindMethodVariables(method);
        }
    }

    private void parseConversionMode() {
        if (isConversionMethodSet()) {
            // 检查 conversion method 是否存在
            ExecutableElement method = findPublicStaticMethodInClass(conversionMethodClass, conversionMethodName, targetFieldType);
            if (null == method) {
                throw new RapidOOOCompileException("Method[public static " + targetFieldType.toString() + " " + conversionMethodName + "(...)] not found \nin " + conversionMethodClass.toString() + " class.");
            }
            // 检查 conversion method 方法中的所有参数是否存在在 OOO 中
            conversionTargetParamFields = findConversionMethodVariables(method);
        }
        if (isInverseConversionMethodSet()) {
            // 检查 conversion method 是否存在
            ExecutableElement method = findPublicStaticMethodInClass(conversionMethodClass, inverseConversionMethodName, ElementUtil.getTypeName(void.class));
            if (null == method) {
                throw new RapidOOOCompileException("Method[public static void " + inverseConversionMethodName + "(...)] not found \nin " + conversionMethodClass.toString() + " class.");
            }
            // 检查 conversion method 方法中的所有参数是否存在在 OOO 中
            inverseConversionTargetParamFields = findConversionMethodVariables(method);
        }
    }

    /**
     * 检查 bind method 方法中的所有参数是否存在在 OOO 中
     */
    private HashMap<String, IOOOVariable> findBindMethodVariables(ExecutableElement method) {
        HashMap<String, IOOOVariable> variableElements = new LinkedHashMap<>();
        for (VariableElement ve : method.getParameters()) {
            if (
                    TextUtil.equals(ve.getSimpleName().toString(), "self")
                            &&
                            // TODO: 2019-06-13 wangjie validate class type simple name here, need optimize
                            TextUtil.equals(ve.asType().toString(), oooEntry.getTargetClassSimpleName())
            ) {
                SelfObjectVariable selfObjectVariable = new SelfObjectVariable(ve.getSimpleName().toString());
                variableElements.put(selfObjectVariable.fieldName(), selfObjectVariable);
            } else {
                // 检查某个参数是否存在在 OOO 中
                IOOOVariable fieldEntry = findFieldInOOO(ve);
                if (null == fieldEntry) {
                    throw new RapidOOOCompileException("Can not found field[" + ve.getSimpleName() + "-" + method.getSimpleName() + "] \nin " + oooEntry.getTargetClassSimpleName());
                }
                variableElements.put(fieldEntry.fieldName(), fieldEntry);
            }
        }

        return variableElements;
    }

    /**
     * 检查 conversion method 方法中的所有参数是否存在在 OOO 中
     */
    private HashMap<String, IOOOVariable> findConversionMethodVariables(ExecutableElement method) {
        HashMap<String, IOOOVariable> variableElements = new LinkedHashMap<>();
        for (VariableElement ve : method.getParameters()) {
            if (
                    TextUtil.equals(ve.getSimpleName().toString(), "self")
                            &&
                            // TODO: 2019-06-13 wangjie validate class type simple name here, need optimize
                            TextUtil.equals(MoreTypes.asTypeElement(ve.asType()).getSimpleName().toString(), oooEntry.getTargetClassSimpleName())
            ) {
                SelfObjectVariable selfObjectVariable = new SelfObjectVariable(ve.getSimpleName().toString());
                variableElements.put(selfObjectVariable.fieldName(), selfObjectVariable);
            } else if (
                    TextUtil.equals(ve.getSimpleName().toString(), "other")
                            &&
                            // TODO: 2019-06-13 wangjie validate class type simple name here, need optimize
                            TextUtil.equals(MoreTypes.asTypeElement(ve.asType()).getSimpleName().toString(), oooEntry.getFromSimpleName())
            ) {
                OtherObjectVariable otherObjectVariable = new OtherObjectVariable(ve.getSimpleName().toString(), TextUtil.firstCharLower(oooEntry.getFromSimpleName()));
                variableElements.put(otherObjectVariable.fieldName(), otherObjectVariable);
            } else {
                // 检查某个参数是否存在在 OOO 中
                IOOOVariable fieldEntry = findFieldInOOO(ve);
                if (null != fieldEntry) {
                    variableElements.put(fieldEntry.fieldName(), fieldEntry);
                } else {
                    // TODO: 2019-06-14 wangjie 校验 ve 是否存在 other class 里面？
                    OtherFieldVariable otherFieldVariable = new OtherFieldVariable(ve, TextUtil.firstCharLower(oooEntry.getFromSimpleName()));
                    variableElements.put(otherFieldVariable.fieldName(), otherFieldVariable);
                }
            }
        }

        return variableElements;
    }


    /**
     * 检查某个参数是否存在在 OOO 中
     */
    private IOOOVariable findFieldInOOO(VariableElement variableElement) {
        // TODO: 2019-06-13 wangjie 支持父类字段？
        for (Map.Entry<String, OOOFieldEntry> e : oooEntry.getAllContinuingFields().entrySet()) {
            OOOFieldEntry fieldEntry = e.getValue();
            if (
                // 属性名一样
                    TextUtil.equals(variableElement.getSimpleName().toString(), fieldEntry.getSimpleName())
                            &&
                            // 属性类型一样
                            ElementUtil.isSameType(variableElement.asType(), fieldEntry.getTypeName())
            ) {
                return fieldEntry;
            }
        }
        for (Map.Entry<String, OOOConversionEntry> e : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = e.getValue();
            if (
                // 属性名一样
                    TextUtil.equals(variableElement.getSimpleName().toString(), conversionEntry.getTargetFieldName())
                            &&
                            // 属性类型一样
                            ElementUtil.isSameType(variableElement.asType(), conversionEntry.getTargetFieldType())
            ) {
                return conversionEntry;
            }
        }


        return null;
    }

    /**
     * 检查对应名字的 method 是否在某个类中存在
     */
    private ExecutableElement findPublicStaticMethodInClass(TypeMirror methodClass, String methodName, TypeName returnType) {
        for (Element e : MoreTypes.asElement(methodClass).getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                // Must public static
                ExecutableElement method = MoreElements.asExecutable(e);
                if (!MoreElements.hasModifiers(Modifier.STATIC).apply(method)
                        ||
                        !MoreElements.hasModifiers(Modifier.PUBLIC).apply(method)
                ) {
                    continue;
                }
                if (
                        TextUtil.equals(methodName, method.getSimpleName().toString())
                        &&
                                ElementUtil.isSameType(method.getReturnType(), returnType)
                ) {
                    return method;
                }
            }
        }
        return null;
    }


    public OOOEntry getOooEntry() {
        return oooEntry;
    }

    public String getTargetFieldName() {
        return targetFieldName;
    }

    public TypeName getTargetFieldType() {
        return targetFieldType;
    }

    public String getTargetFieldTypeId() {
        return targetFieldTypeId;
    }

    public String getBindMethodName() {
        return bindMethodName;
    }

    public String getInverseBindMethodName() {
        return inverseBindMethodName;
    }


    public String getConversionMethodName() {
        return conversionMethodName;
    }

    public String getInverseConversionMethodName() {
        return inverseConversionMethodName;
    }

    public TypeName getBindMethodClassType() {
        return bindMethodClassType;
    }

    public TypeName getConversionMethodClassType() {
        return conversionMethodClassType;
    }

    public OOOControlMode getControlMode() {
        return controlMode;
    }

    public boolean isBindMethodSet() {
        return !AnnoUtil.oooParamIsNotSet(bindMethodName);
    }

    public boolean isInverseBindMethodSet() {
        return !AnnoUtil.oooParamIsNotSet(inverseBindMethodName);
    }

    public boolean isConversionMethodSet() {
        return !AnnoUtil.oooParamIsNotSet(conversionMethodName);
    }

    public boolean isInverseConversionMethodSet() {
        return !AnnoUtil.oooParamIsNotSet(inverseConversionMethodName);
    }

    public HashMap<String, IOOOVariable> getBindTargetParamFields() {
        return bindTargetParamFields;
    }

    public HashMap<String, IOOOVariable> getInverseBindTargetParamFields() {
        return inverseBindTargetParamFields;
    }

    public HashMap<String, IOOOVariable> getConversionTargetParamFields() {
        return conversionTargetParamFields;
    }

    public HashMap<String, IOOOVariable> getInverseConversionTargetParamFields() {
        return inverseConversionTargetParamFields;
    }

    public boolean isTargetFieldTypeId() {
        return null != targetFieldTypeId && !AnnoUtil.oooParamIsNotSet(targetFieldTypeId);
    }

    public TypeName getAttachFieldType() {
        // attach 属性类型与 target 一样
        return targetFieldType;
    }

    public String getAttachFieldName() {
        return attachFieldName;
    }

    @Override
    public String fieldName() {
        return targetFieldName;
    }

    @Override
    public String inputCode() {
        return "this." + targetFieldName;
    }
}
