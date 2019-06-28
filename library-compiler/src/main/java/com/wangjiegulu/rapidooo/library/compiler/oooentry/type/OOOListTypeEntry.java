package com.wangjiegulu.rapidooo.library.compiler.oooentry.type;

import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-24.
 */
public class OOOListTypeEntry extends OOOTypeEntry {
    private TypeName argumentType;
//    private TypeName instanceType;

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
        return OOOTypeEnum.LIST;
    }

    private void init() {
        if (typeName instanceof ParameterizedTypeName) {
            List<TypeName> tps = ((ParameterizedTypeName) typeName).typeArguments;
            if (null != tps && tps.size() == 1) {
                argumentType = tps.get(0);
            }

//            if (fullTypeEntry.toString().matches("java\\.util\\.List<.+>")) {
//                instanceType = TypeName.get(ArrayList.class);
//            } else {
//                instanceType = ((ParameterizedTypeName) fullTypeEntry).rawType;
//            }
        }

    }

//    public TypeName getInstanceType() {
//        return instanceType;
//    }

    @Override
    public boolean isRefId() {
        return false;
    }

    public TypeName getArgumentType() {
        return argumentType;
    }


}
