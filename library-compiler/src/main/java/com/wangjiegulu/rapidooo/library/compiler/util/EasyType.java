package com.wangjiegulu.rapidooo.library.compiler.util;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 3/15/16.
 */
public class EasyType {

    public static TypeName bestGuess(String type) {
        switch (type) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                int left = type.indexOf('<');
                if (left != -1) {
                    ClassName typeClassName = ClassName.bestGuess(type.substring(0, left));
                    List<TypeName> typeArguments = new ArrayList<>();
                    do {
                        typeArguments.add(WildcardTypeName.subtypeOf(Object.class));
                        left = type.indexOf('<', left + 1);
                    } while (left != -1);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArguments.toArray(new TypeName[typeArguments.size()]));
                }
                return ClassName.bestGuess(type);
        }
    }

    /**
     * @param type
     * @return
     */
    public static TypeName bestGuessDeepWildcard(String type) {
        switch (type) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                int left = type.indexOf('<');
                int right = type.indexOf('>');
                if (-1 != left && -1 != right) {
                    ClassName typeClassName = ClassName.bestGuess(type.substring(0, left));
                    List<TypeName> typeArguments = new ArrayList<>();
                    do {
                        typeArguments.add(WildcardTypeName.subtypeOf(bestGuess(type.substring(left + 1, right))));
                        left = type.indexOf('<', left + 1);
                        right = type.indexOf('>', right - 1);
                    } while (left != -1);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArguments.toArray(new TypeName[typeArguments.size()]));
                }
                return ClassName.bestGuess(type);
        }
    }

    public static TypeName bestGuessDeep2(String type) {
        return bestGuessDeep2(type, null);
    }
    public static TypeName bestGuessDeep2(String type, Func1R<String, TypeName> func) {
        switch (type) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                int left = type.indexOf('<');
                int right = type.indexOf('>');
                if (-1 != left && -1 != right) {
                    ClassName typeClassName = ClassName.bestGuess(type.substring(0, left));
                    List<TypeName> typeArguments = new ArrayList<>();
                    do {
                        String _type = type.substring(left + 1, right).trim();
                        if(isRefId(_type) && null != func){
                            typeArguments.add(func.call(_type));
                        }else{
                            typeArguments.add(bestGuess(_type));
                        }
                        left = type.indexOf('<', left + 1);
                        right = type.indexOf('>', right - 1);
                    } while (left != -1);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArguments.toArray(new TypeName[typeArguments.size()]));
                }
                return ClassName.bestGuess(type);
        }
    }

    public static boolean isListType(String str){
        return str.matches("java\\.util\\.List<.+>");
    }
    public static boolean isMapType(String str){
        return str.matches("java\\.util\\.Map<.+,.+>");
    }
    public static boolean isArrayType(String str){
        return str.matches(".+\\[]");
    }

    public static boolean isListType(TypeName typeName){
        return isListType(typeName.toString()) && typeName instanceof ParameterizedTypeName;
    }
    public static boolean isMapType(TypeName typeName){
        return isMapType(typeName.toString()) && typeName instanceof ParameterizedTypeName;
    }
    public static boolean isArrayType(TypeName typeName){
        return isArrayType(typeName.toString()) && typeName instanceof ArrayTypeName;
    }

    public static boolean isRefId(String str){
        return null != str && str.startsWith("#");
    }

}

