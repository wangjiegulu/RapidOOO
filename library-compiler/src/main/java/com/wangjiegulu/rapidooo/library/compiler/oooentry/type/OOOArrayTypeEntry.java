package com.wangjiegulu.rapidooo.library.compiler.oooentry.type;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-24.
 */
public class OOOArrayTypeEntry extends OOOTypeEntry{
    private TypeName arrayItemTypeName;
    private boolean isRefId = false;

    @Override
    public void initialize(String idExp) {
        super.initialize(idExp);
        isRefId = EasyType.isRefId(idExp);
        init();
    }

    @Override
    public void initialize(TypeName typeName) {
        super.initialize(typeName);
        init();
    }

    @Override
    public OOOTypeEnum getTypeEnum() {
        return OOOTypeEnum.ARRAY;
    }

    private void init() {
        if (typeName instanceof ArrayTypeName) {
            arrayItemTypeName = ((ArrayTypeName)typeName).componentType;
        }
    }

    @Override
    public boolean isRefId() {
        return isRefId;
    }

    public TypeName getArrayItemTypeName() {
        return arrayItemTypeName;
    }


}
