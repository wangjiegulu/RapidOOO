package com.wangjiegulu.rapidooo.library.compiler.oooentry.type;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-24.
 */
public class OOOObjectTypeEntry extends OOOTypeEntry{
    private HashMap<String, TypeName> argumentTypeMapper = new LinkedHashMap<>();
    private List<String> argumentNames = new ArrayList<>();
    private boolean isRefId = false;

    @Override
    public void initialize(String idExp) {
        super.initialize(idExp);
        isRefId = EasyType.isRefId(idExp);
    }

    @Override
    public void initialize(TypeName typeName) {
        super.initialize(typeName);
        if(typeName instanceof ParameterizedTypeName){
            for (TypeName tn : ((ParameterizedTypeName) typeName).typeArguments) {
                argumentTypeMapper.put(tn.toString(), tn);
                argumentNames.add(tn.toString());
            }
        }
    }

    @Override
    public OOOTypeEnum getTypeEnum() {
        return OOOTypeEnum.OBJECT;
    }

    @Override
    public boolean isRefId() {
        return isRefId;
    }
}
