package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-20.
 */
public class OOOTypeEntry {
    public enum OOOTypeEnum {
        LIST, MAP, OBJECT
    }

    private TypeName typeName;
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
            typeName = parseBestGuess(oooConversionEntry, idExp);
            oooTypeEnum = OOOTypeEnum.MAP;
        } else {
            if (EasyType.isRefId(idExp)) {
                typeName = oooConversionEntry.getOooEntry().getOoosEntry().queryTypeById(idExp).getTargetClassType();
                isRefId = true;
            } else {
                typeName = ClassName.bestGuess(idExp);
            }
            oooTypeEnum = OOOTypeEnum.OBJECT;
        }
    }

    public void parse(TypeName typeName){
        this.typeName = typeName;
        this.oooTypeEnum = OOOTypeEnum.OBJECT;
        if(typeName instanceof ParameterizedTypeName){
            for(TypeName tn : ((ParameterizedTypeName) typeName).typeArguments){
                argumentTypeMapper.put(tn.toString(), tn);
                argumentNames.add(tn.toString());
            }
        }
    }

    public boolean isList() {
        return OOOTypeEnum.LIST == oooTypeEnum;
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
    public boolean hasArgumentRefId(){
        return hasArgumentRefId;
    }

    public boolean isRefId() {
        return isRefId;
    }

    public TypeName get(int index){
        return argumentTypeMapper.get(argumentNames.get(index));
    }

    private TypeName parseBestGuess(OOOConversionEntry oooConversionEntry, String idExp) {
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
                        if (_type.startsWith("#")) {
                            OOOEntry queryTypeResult = oooConversionEntry.getOooEntry().getOoosEntry().queryTypeById(_type);
                            if (null == queryTypeResult) {
                                throw new RapidOOOCompileException("Id[" + idExp + ": " + _type + "] not found.\n" + oooConversionEntry.getOooEntry().getOoosEntry().getOooGenerator().getGeneratorClassType());
                            }
                            argumentType = queryTypeResult.getTargetClassType();
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
                return ClassName.bestGuess(idExp);
        }
    }


}
