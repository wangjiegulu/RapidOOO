package com.wangjiegulu.rapidooo.library.compiler.util;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class PoetUtil {

    public static GetterSetterMethodNames generateGetterSetterMethodName(String fieldName, TypeName fieldType) {
        GetterSetterMethodNames getterSetterMethodNames = new GetterSetterMethodNames();

        String firstCharUpperFieldName = TextUtil.firstCharUpper(fieldName);

        if (fieldType == TypeName.BOOLEAN) {
            if ("is".equalsIgnoreCase(fieldName.substring(0, 2))) {
                getterSetterMethodNames.setGetterMethodName(fieldName);
                getterSetterMethodNames.setSetterMethodName("set" + TextUtil.firstCharUpper(fieldName.substring(2)));
            } else {
                getterSetterMethodNames.setGetterMethodName("is" + firstCharUpperFieldName);
                getterSetterMethodNames.setSetterMethodName("set" + firstCharUpperFieldName);
            }
        } else if (Boolean.class.getCanonicalName().equals(fieldType.toString())) {
            if ("is".equalsIgnoreCase(fieldName.substring(0, 2))) {
                getterSetterMethodNames.setGetterMethodName("get" + fieldName.substring(2));
                getterSetterMethodNames.setSetterMethodName("set" + TextUtil.firstCharUpper(fieldName.substring(2)));
            } else {
                getterSetterMethodNames.setGetterMethodName("get" + firstCharUpperFieldName);
                getterSetterMethodNames.setSetterMethodName("set" + firstCharUpperFieldName);
            }
        } else {
            getterSetterMethodNames.setGetterMethodName("get" + firstCharUpperFieldName);
            getterSetterMethodNames.setSetterMethodName("set" + firstCharUpperFieldName);
        }
        return getterSetterMethodNames;
    }
    public static MethodSpec.Builder obtainGetterMethodsBuilder(String fieldName, TypeName fieldType, GetterSetterMethodNames getterSetterMethodNames) {
        return MethodSpec.methodBuilder(getterSetterMethodNames.getGetterMethodName())
                .addModifiers(Modifier.PUBLIC)
                .returns(fieldType)
                .addStatement("return " + fieldName);
    }

    public static MethodSpec.Builder obtainSetterMethodsBuilderDefault(String fieldName, TypeName fieldType, GetterSetterMethodNames getterSetterMethodNames) {
        return MethodSpec.methodBuilder(getterSetterMethodNames.getSetterMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldType, fieldName)
                .returns(void.class)
                .addStatement("this." + fieldName + " = " + fieldName);
    }

    /**
     * Default extra getter method for Boolean field.
     *
     * public boolean getBot(boolean defaultValue) {
     *   return null == isBot ? defaultValue : isBot;
     * }
     * @param fieldName
     * @param getterSetterMethodNames
     */
    public static MethodSpec.Builder obtainExtraBooleanGetterMethodsBuilder(String fieldName, GetterSetterMethodNames getterSetterMethodNames) {
        return MethodSpec.methodBuilder(getterSetterMethodNames.getGetterMethodName())
                .addModifiers(Modifier.PUBLIC)
                .addParameter(boolean.class, "defaultValue")
                .returns(boolean.class)
                .addStatement("return null == " + fieldName + " ? defaultValue : " + fieldName);
    }
}
