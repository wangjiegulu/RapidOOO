package com.wangjiegulu.rapidooo.library.compiler.util;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.WildcardTypeName;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
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
        return str.matches("java\\.util\\..*List<.+>");
    }
    public static boolean isMapType(String str){
        return str.matches("java\\.util\\..*Map<.+,.+>");
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


    // TypeName parse
    public static TypeName parseTypeName(String idExp) {
        int arrayHeight = 0;
        String tempIdExp = idExp;
        while (tempIdExp.endsWith("[]")) {
            tempIdExp = tempIdExp.substring(0, tempIdExp.length() - 2);
            arrayHeight++;
        }
        TypeName resultTypeName = parseInnerTypeName(tempIdExp);
//        arrayItemTypeName = resultTypeName;
        for(int i = 0; i < arrayHeight; i++){
            resultTypeName = ArrayTypeName.of(resultTypeName);
        }
        if(arrayHeight > 0){
            LogUtil.logger("OOOTypeEntry, array ------>" + resultTypeName);
        }
        return resultTypeName;
    }
    private static TypeName parseInnerTypeName(String idExp) {
        switch (idExp) {
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
        }

        int left = idExp.indexOf('<');
        int right = idExp.lastIndexOf('>');
        if (-1 != left && -1 != right) {
            // eg: java.util.List / HashMap
            String typeClassNameStr = idExp.substring(0, left);
            ClassName typeClassName;
            if (EasyType.isRefId(typeClassNameStr)) {
                typeClassName = ClassName.bestGuess(queryRefTypeName(typeClassNameStr).toString());
            } else {
                typeClassName = ClassName.bestGuess(typeClassNameStr);
            }

            // eg: #id_Foo<Bar> / java.lang.String, Foo
            String _type = idExp.substring(left + 1, right).trim();
            List<TypeName> typeArgs = new ArrayList<>();

            // eg: java.lang.String, Foo
            if (_type.contains(",")) {
                String[] _subTypes = _type.split(",");
                for (String _subType : _subTypes) {
                    // eg: java.lang.String
                    typeArgs.add(parseTypeName(_subType.trim()));
                }
            } else { // eg: java.lang.String
                typeArgs.add(parseTypeName(_type.trim()));
            }
            return ParameterizedTypeName.get(typeClassName, typeArgs.toArray(new TypeName[typeArgs.size()]));
        }

        if (EasyType.isRefId(idExp)) {
            return queryRefTypeName(idExp);
        }
        return ClassName.bestGuess(idExp);
    }

    public static TypeName queryRefTypeName(String id) {
        OOOEntry queryTypeResult = OOOSEntry.queryTypeById(id);
        if (null == queryTypeResult) {
//            throw new RapidOOOCompileException("Id[" + id + "] not found.\n" + oooConversionEntry.getOooEntry().getOoosEntry().getOooGenerator().getGeneratorClassType());
            throw new RapidOOOCompileException("Id[" + id + "] not found.");
        }
        return queryTypeResult.getTargetClassType();
    }

}

