package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-20.
 */
public class OOOTypeEntry {
    public enum OOOTypeEnum {
        LIST, ARRAY, MAP, OBJECT
    }

    private TypeName typeName;
    private TypeName arrayItemTypeName;
    private HashMap<String, TypeName> argumentTypeMapper = new LinkedHashMap<>();
    private List<String> argumentNames = new ArrayList<>();
    private OOOTypeEnum oooTypeEnum;
    private boolean isRefId = false;
    private boolean hasArgumentRefId = false;

    public void parse(OOOConversionEntry oooConversionEntry, String idExp) {
        if (AnnoUtil.oooParamIsNotSet(idExp)) {
            return;
        }

        if (EasyType.isListType(idExp)) {
            typeName = parseBestGuess(oooConversionEntry, idExp);
            oooTypeEnum = OOOTypeEnum.LIST;
        } else if (EasyType.isMapType(idExp)) {
            // TODO: 2019-06-21 wangjie
            typeName = parseBestGuess(oooConversionEntry, idExp);
            oooTypeEnum = OOOTypeEnum.MAP;
        } else if (EasyType.isArrayType(idExp)) {
            typeName = parseBestGuess(oooConversionEntry, idExp);
            oooTypeEnum = OOOTypeEnum.ARRAY;
            isRefId = EasyType.isRefId(idExp);
        } else {
            if (EasyType.isRefId(idExp)) {
                typeName = OOOSEntry.queryTypeById(idExp).getTargetClassType();
                isRefId = true;
            } else {
                typeName = ClassName.bestGuess(idExp);
            }
            oooTypeEnum = OOOTypeEnum.OBJECT;
        }
    }

    public void parse(TypeName typeName) {
        this.typeName = typeName;
        if(EasyType.isListType(typeName)){
            oooTypeEnum = OOOTypeEnum.LIST;
        } else if(EasyType.isMapType(typeName)){
            oooTypeEnum = OOOTypeEnum.MAP;
        } else if(EasyType.isArrayType(typeName)){
            oooTypeEnum = OOOTypeEnum.ARRAY;
            arrayItemTypeName = ((ArrayTypeName)typeName).componentType;
        } else {
            oooTypeEnum = OOOTypeEnum.OBJECT;
        }
        initTypeName(typeName);
    }

    private void initTypeName(TypeName typeName){
        if(typeName instanceof ParameterizedTypeName){
            for (TypeName tn : ((ParameterizedTypeName) typeName).typeArguments) {
                argumentTypeMapper.put(tn.toString(), tn);
                argumentNames.add(tn.toString());
            }
        }
    }

    public boolean isList() {
        return OOOTypeEnum.LIST == oooTypeEnum;
    }
    public boolean isArray() {
        return OOOTypeEnum.ARRAY == oooTypeEnum;
    }

    public boolean isMap() {
        return OOOTypeEnum.MAP == oooTypeEnum;
    }

    public boolean isObject() {
        return OOOTypeEnum.OBJECT == oooTypeEnum;
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public OOOTypeEnum getOooTypeEnum() {
        return oooTypeEnum;
    }

    public HashMap<String, TypeName> getArgumentTypes() {
        return argumentTypeMapper;
    }

    public boolean hasArgumentRefId() {
        return hasArgumentRefId;
    }

    public TypeName getArrayItemTypeName() {
        return arrayItemTypeName;
    }

    public boolean isRefId() {
        return isRefId;
    }

    public TypeName get(int index) {
        return argumentTypeMapper.get(argumentNames.get(index));
    }

    private TypeName parseBestGuess(OOOConversionEntry oooConversionEntry, String idExp) {
        int arrayHeight = 0;
        String tempIdExp = idExp;
        while (tempIdExp.endsWith("[]")) {
            tempIdExp = tempIdExp.substring(0, tempIdExp.length() - 2);
            arrayHeight++;
        }
        TypeName resultTypeName = parseBestGuessInternal(oooConversionEntry, tempIdExp);
        arrayItemTypeName = resultTypeName;
        for(int i = 0; i < arrayHeight; i++){
            resultTypeName = ArrayTypeName.of(resultTypeName);
        }
        if(arrayHeight > 0){
            LogUtil.logger("OOOTypeEntry, array ------>" + resultTypeName);
        }
        return resultTypeName;
    }

    private TypeName parseBestGuessInternal(OOOConversionEntry oooConversionEntry, String idExp) {
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
            default:
                int left = idExp.indexOf('<');
                int right = idExp.indexOf('>');
                if (-1 != left && -1 != right) {
                    ClassName typeClassName = ClassName.bestGuess(idExp.substring(0, left));
                    List<TypeName> typeArgs = new ArrayList<>();
                    do {
                        String _type = idExp.substring(left + 1, right).trim();
                        TypeName argumentType;
                        if (EasyType.isRefId(_type)) {
                            argumentType = queryRefTypeName(oooConversionEntry, _type);
                            hasArgumentRefId = true;
                        } else {
                            argumentType = EasyType.bestGuess(_type);
                        }

                        argumentNames.add(_type);
                        argumentTypeMapper.put(_type, argumentType);
                        typeArgs.add(argumentType);
                        left = idExp.indexOf('<', left + 1);
                        right = idExp.indexOf('>', right - 1);
                    } while (left != -1);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArgs.toArray(new TypeName[typeArgs.size()]));
                }

                if (EasyType.isRefId(idExp)) {
                    return queryRefTypeName(oooConversionEntry, idExp);
                }
                return ClassName.bestGuess(idExp);
        }
    }

    private TypeName queryRefTypeName(OOOConversionEntry oooConversionEntry, String id) {
        OOOEntry queryTypeResult = OOOSEntry.queryTypeById(id);
        if (null == queryTypeResult) {
            throw new RapidOOOCompileException("Id[" + id + "] not found.\n" + oooConversionEntry.getOooEntry().getOoosEntry().getOooGenerator().getGeneratorClassType());
        }
        return queryTypeResult.getTargetClassType();
    }


}
