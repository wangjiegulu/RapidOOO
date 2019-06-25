package com.wangjiegulu.rapidooo.library.compiler.oooentry.type;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-24.
 */
public class OOOMapTypeEntry extends OOOTypeEntry {
    private TypeName keyTypeName;
    private TypeName valueTypeName;
    private TypeName rawType;

    @Override
    public void initialize(String idExp) {
        super.initialize(idExp);
        init();
    }

    @Override
    public void initialize(TypeName typeName) {
        super.initialize(typeName);
        init();
    }

    @Override
    public OOOTypeEnum getTypeEnum() {
        return OOOTypeEnum.MAP;
    }

    private void init() {
        if (typeName instanceof ParameterizedTypeName) {
            List<TypeName> tps = ((ParameterizedTypeName) typeName).typeArguments;
            if (null != tps && tps.size() == 2) {
                keyTypeName = tps.get(0);
                valueTypeName = tps.get(1);
            }
        }

        if (typeName.toString().matches("java\\.util\\.Map<.+>")) {
            rawType = TypeName.get(HashMap.class);
        } else {
            rawType = ((ParameterizedTypeName) typeName).rawType;
        }
    }

    public boolean isHashMap(){
        return ElementUtil.isSameType(rawType, HashMap.class);
    }

    @Override
    public boolean isRefId() {
        return false;
    }

    public TypeName getKeyTypeName() {
        return keyTypeName;
    }

    public TypeName getValueTypeName() {
        return valueTypeName;
    }

    public TypeName getRawType() {
        return rawType;
    }
}
