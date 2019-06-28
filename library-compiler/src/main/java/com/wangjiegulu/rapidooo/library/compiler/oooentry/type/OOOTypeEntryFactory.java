package com.wangjiegulu.rapidooo.library.compiler.oooentry.type;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.api.func.Func1R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-24.
 */
public class OOOTypeEntryFactory {
    private static HashMap<Func1R<String, Boolean>, Func1R<String, OOOTypeEntry>> createByIdExpMap = new LinkedHashMap<>();
    private static HashMap<Func1R<TypeName, Boolean>, Func1R<TypeName, OOOTypeEntry>> createByTypeNameMap = new LinkedHashMap<>();

    static {
        createByIdExpMap.put(EasyType::isListType, it -> new OOOListTypeEntry().prepare(it));
        createByIdExpMap.put(EasyType::isArrayType, it -> new OOOArrayTypeEntry().prepare(it));
        createByIdExpMap.put(EasyType::isMapType, it -> new OOOMapTypeEntry().prepare(it));
        createByIdExpMap.put(it -> true, it -> new OOOObjectTypeEntry().prepare(it));

        createByTypeNameMap.put(EasyType::isListType, it -> new OOOListTypeEntry().prepare(it));
        createByTypeNameMap.put(EasyType::isArrayType, it -> new OOOArrayTypeEntry().prepare(it));
        createByTypeNameMap.put(EasyType::isMapType, it -> new OOOMapTypeEntry().prepare(it));
        createByTypeNameMap.put(it -> true, it -> new OOOObjectTypeEntry().prepare(it));
    }

    public static OOOTypeEntry create(String idExp){
        for(Map.Entry<Func1R<String, Boolean>, Func1R<String, OOOTypeEntry>> entry : createByIdExpMap.entrySet()){
            if(entry.getKey().call(idExp)){
                return entry.getValue().call(idExp);
            }
        }
        throw new RapidOOOCompileException("ERROR! \nInOOOTypeEntry::create, idExp: " + idExp);
    }

    public static OOOTypeEntry create(TypeName typeName){
        for(Map.Entry<Func1R<TypeName, Boolean>, Func1R<TypeName, OOOTypeEntry>> entry : createByTypeNameMap.entrySet()){
            if(entry.getKey().call(typeName)){
                return entry.getValue().call(typeName);
            }
        }
        throw new RapidOOOCompileException("ERROR! \nInOOOTypeEntry::create, fullTypeEntry: " + typeName);
    }
}
