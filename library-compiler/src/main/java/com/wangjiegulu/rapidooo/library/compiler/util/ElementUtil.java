package com.wangjiegulu.rapidooo.library.compiler.util;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Optional;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.RapidOOOConstants;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 3/16/16.
 */
public class ElementUtil {

    public static TypeName getTypeName(Element element) {
        return ClassName.get(element.asType());
    }

    public static TypeName getTypeName(Type type) {
        return TypeName.get(type);
    }

    public static TypeName getTypeName(TypeMirror typeMirror) {
        return TypeName.get(typeMirror);
    }

    public static ClassName getClassName(TypeMirror typeMirror) {
        return ClassName.get(MoreTypes.asTypeElement(typeMirror));
    }

    public static Name getName(TypeMirror typeMirror) {
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
        LogUtil.logger("isSameTypeisSameTypeisSameType: " + type1 + ", " + type2);
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
                    if (ElementUtil.isSameType(interf, ClassName.bestGuess(RapidOOOConstants.CLASS_NAME_PARCELABLE))) {
                        return true;
                    }
                }
            } else {
                currentClass = null;
            }
        } while (null != currentClass);
        return false;
    }

    public static boolean isSubType(TypeMirror typeMirror, Class clazz) {
        return isSubType(typeMirror, clazz.getCanonicalName());
    }

    public static boolean isSubType(TypeMirror typeMirror, String canonicalName) {
        LogUtil.logger("[isSubType]===>typeMirror: " + typeMirror + ", isSubType: " + canonicalName);
        Types types = GlobalEnvironment.getProcessingEnv().getTypeUtils();
        Elements elements = GlobalEnvironment.getProcessingEnv().getElementUtils();
        return types.isAssignable(typeMirror, elements.getTypeElement(canonicalName).asType());
    }


    private static WildcardType WILDCARD_TYPE_NULL = GlobalEnvironment.getProcessingEnv().getTypeUtils().getWildcardType(null, null);
    private static Map<String,DeclaredType> cachedParentTypes = new HashMap<String, DeclaredType>();
    public static boolean isAssignable(TypeMirror type, Class clazz) {
        return isAssignable(type, GlobalEnvironment.getProcessingEnv().getElementUtils().getTypeElement(clazz.getCanonicalName()));
    }
    public static boolean isAssignable(TypeMirror type, TypeElement typeElement) {

        // Have we used this type before?
        DeclaredType parentType = cachedParentTypes.get(typeElement.getQualifiedName().toString());
        if (parentType == null) {
            // How many generic type parameters does this typeElement require?
            int genericsCount = typeElement.getTypeParameters().size();

            // Fill the right number of types with nulls
            TypeMirror[] types = new TypeMirror[genericsCount];
            for (int i = 0; i < genericsCount; i++) {
                types[i] = WILDCARD_TYPE_NULL;
            }

            // Locate the correct DeclaredType to match with the type
            parentType = GlobalEnvironment.getProcessingEnv().getTypeUtils().getDeclaredType(typeElement, types);

            // Remember this DeclaredType
            cachedParentTypes.put(typeElement.getQualifiedName().toString(), parentType);
        }

        // Is the given type able to be assigned as the typeElement?
        return GlobalEnvironment.getProcessingEnv().getTypeUtils().isAssignable(type, parentType);
    }

    public static boolean isParcelable(Element field) {
        return isParcelable(field.asType());
    }

    public static boolean isParcelable(TypeMirror fieldType) {
        TypeName typeName = getTypeName(fieldType);
        if (typeName.isPrimitive() || typeName.isBoxedPrimitive()) {
            return true;
        }
//        TypeMirror fieldType = field.asType();
        if (isSubType(fieldType, String.class) ||
//                isAssignable(fieldType, Map.class) ||
//                isAssignable(fieldType, List.class) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_BUNDLE) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_PERSISTABLE_BUNDLE) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_PARCELABLE) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_SPARSE_ARRAY) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_IBINDER) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_SIZE) ||
                isSubType(fieldType, RapidOOOConstants.CLASS_NAME_SIZEF) ||
                isSubType(fieldType, Serializable.class)
        ) {
            return true;
        }

        if(isAssignable(fieldType, List.class)){
            List<? extends TypeParameterElement> typeParameters = MoreTypes.asTypeElement(fieldType).getTypeParameters();
            if(typeParameters.size() == 1){
                return isParcelable(typeParameters.get(0).asType());
            }
        } else if(isAssignable(fieldType, Map.class)){
            List<? extends TypeParameterElement> typeParameters = MoreTypes.asTypeElement(fieldType).getTypeParameters();
            if(typeParameters.size() == 2){
                return isParcelable(typeParameters.get(0).asType()) && isParcelable(typeParameters.get(1).asType());
            }
        }

        return false;
    }

}
