package com.wangjiegulu.rapidooo.library.compiler.util;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 3/16/16.
 */
public class ElementUtil {
    /**
     * 两组参数类型相同
     */
    public static boolean deepSame(List<? extends VariableElement> _this, List<? extends VariableElement> _that) {
        if (null == _this && null == _that) {
            return true;
        }

        if (null == _this || null == _that) {
            return false;
        }

        if (_this.size() != _that.size()) {
            return false;
        }

        for (int i = 0, len = _this.size(); i < len; i++) {
            VariableElement _thisEle = _this.get(i);
            VariableElement _thatEle = _that.get(i);

            if (!MoreElements.asType(_thisEle).getQualifiedName().toString()
                    .equals(MoreElements.asType(_thatEle).getQualifiedName().toString())) {
                return false;
            }
        }

        return true;
    }

    public static boolean isSameType(TypeMirror type1, TypeMirror type2) {
        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isSameType(type1, type2);
    }

    public static boolean isSameType(TypeMirror type1, Class<?> type2) {
        return equals(MoreTypes.asTypeElement(type1).getQualifiedName().toString(), type2.getCanonicalName());
//        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isSameType(type1, GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(type2.getCanonicalName()).asType());
    }

    public static boolean isSameType(Class<?> type1, Class<?> type2) {
        return equals(type1.getCanonicalName(), type2.getCanonicalName());
//        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isSameType(
//                GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(type2.getCanonicalName()).asType(),
//                GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(type2.getCanonicalName()).asType()
//        );
    }

    public static boolean isSameType(TypeMirror type1, TypeName type2) {
        return equals(ClassName.get(type1).toString(), type2.toString());
    }

    public static boolean isSameType(TypeName type1, TypeName type2) {
        return equals(type1.toString(), type2.toString());
    }

    public static boolean isSameSimpleName(TypeMirror type1, TypeName type2) {
        if (ClassName.get(type1).isPrimitive()) {
            return equals(type1.toString(), ((ClassName) type2).simpleName());
        } else {
            return equals(MoreTypes.asTypeElement(type1).getSimpleName().toString(), ((ClassName) type2).simpleName());
        }
    }

    private static boolean equals(String str1, String str2) {
        return null != str1 && str1.equals(str2);
    }

    public static boolean isSameSimpleName(Class type1, TypeName type2) {
        return equals(type1.getCanonicalName(), type2.toString());
    }

    public static Element convertElement(String packageName, String simpleName) {
        PackageElement packageElement = GlobalEnvironment.getElementUtils().getPackageElement(packageName);
        LogUtil.logger("[converElement]packageElement: " + packageElement);
        if (null == packageElement) {
            return null;
        }
        LogUtil.logger("[converElement]packageElement: " + packageElement.getEnclosedElements());
        for (Element element : packageElement.getEnclosedElements()) {
            if (equals(element.getSimpleName().toString(), simpleName)) {
                return element;
            }
        }
        return null;
    }

}
