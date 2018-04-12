package com.wangjiegulu.rapidooo.library.compiler.util;

import com.google.auto.common.MoreElements;

import java.util.List;

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

    public static boolean isSameType(TypeMirror type1, TypeMirror typ22) {
        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isSameType(type1, typ22);
    }

    public static boolean isSameType(TypeMirror type1, Class<?> type2) {
        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isSameType(type1, GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(type2.getCanonicalName()).asType());
    }

    public static boolean isSameType(Class<?> type1, Class<?> type2) {
        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isSameType(
                GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(type2.getCanonicalName()).asType(),
                GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(type2.getCanonicalName()).asType()
        );
    }

}
