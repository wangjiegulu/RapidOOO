package com.wangjiegulu.rapidooo.library.compiler.util;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.lang.reflect.Type;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 3/16/16.
 */
public class ElementUtil {

    public static TypeName getTypeName(Element element){
        return ClassName.get(element.asType());
    }
    public static TypeName getTypeName(Type type){
        return TypeName.get(type);
    }
    public static TypeName getTypeName(TypeMirror typeMirror){
        return TypeName.get(typeMirror);
    }
    public static ClassName getClassName(TypeMirror typeMirror){
        return ClassName.get(MoreTypes.asTypeElement(typeMirror));
    }
    public static Name getName(TypeMirror typeMirror){
        return MoreTypes.asTypeElement(typeMirror).getQualifiedName();
    }


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
        return equals(getSimpleName(type1), getSimpleName(type2));
    }

    public static boolean equals(String str1, String str2) {
        return null != str1 && str1.equals(str2);
    }

    public static boolean isSameName(Class type1, TypeName type2) {
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

    public static boolean isSameSimpleName(TypeMirror type1, String type2) {
        return equals(getSimpleName(type1), type2);
    }

    public static String getSimpleName(TypeName typeName) {
        return typeName.isPrimitive() || typeName == TypeName.VOID ? typeName.toString() : ClassName.bestGuess(typeName.toString()).simpleName();
    }

    public static String getSimpleName(TypeMirror typeMirror) {
        TypeName type1Name = ClassName.get(typeMirror);
        return type1Name.isPrimitive() || type1Name == TypeName.VOID ? type1Name.toString() : MoreTypes.asTypeElement(typeMirror).getSimpleName().toString();
    }


    public static boolean isSupperParcelableInterfaceDeep(Element fromClassElement) {
        Element currentClass = fromClassElement;
        do {
            Optional<DeclaredType> superClass = MoreTypes.nonObjectSuperclass(GlobalEnvironment.getProcessingEnv().getTypeUtils(),
                    GlobalEnvironment.getProcessingEnv().getElementUtils(), (DeclaredType) currentClass.asType());
            if (superClass.isPresent()) {
                currentClass = superClass.get().asElement();
                for (TypeMirror interf : MoreTypes.asTypeElement(currentClass.asType()).getInterfaces()) {
                    if (ElementUtil.isSameType(interf, ClassName.bestGuess("android.os.Parcelable"))) {
                        return true;
                    }
                }
//                    allElements.add(currentClass);
//                    LogUtil.logger("superclass.get().asElement().toString(): " + currentClass.toString());
            } else {
                currentClass = null;
            }
        } while (null != currentClass);
        return false;
    }
}
