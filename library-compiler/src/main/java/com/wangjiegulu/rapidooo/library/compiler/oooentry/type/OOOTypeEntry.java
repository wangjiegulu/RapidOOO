package com.wangjiegulu.rapidooo.library.compiler.oooentry.type;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-20.
 */
public abstract class OOOTypeEntry {
    public enum OOOTypeEnum {
        LIST, ARRAY, MAP, OBJECT
    }
    protected TypeName typeName;

    public OOOTypeEntry prepare(String idExp){
        initialize(idExp);
        return this;
    }

    public OOOTypeEntry prepare(TypeName typeName){
        initialize(typeName);
        return this;
    }

    protected void initialize(String idExp){
        this.typeName = EasyType.parseTypeName(idExp);
    }
    protected void initialize(TypeName typeName){
        this.typeName = typeName;
    }


    public boolean isList() {
        return OOOTypeEnum.LIST == getTypeEnum();
    }
    public boolean isArray() {
        return OOOTypeEnum.ARRAY == getTypeEnum();
    }

    public boolean isMap() {
        return OOOTypeEnum.MAP == getTypeEnum();
    }

    public boolean isObject() {
        return OOOTypeEnum.OBJECT == getTypeEnum();
    }

    public abstract OOOTypeEnum getTypeEnum();

    public TypeName getTypeName() {
        return typeName;
    }

    public abstract boolean isRefId();


}
