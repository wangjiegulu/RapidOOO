package com.wangjiegulu.rapidooo.library.compiler.util;

import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class TextUtil {
    public static  String firstCharUpper(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    public static String firstCharLower(String fieldName) {
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1);
    }

    public static boolean equals(String s1, String s2){
        return null != s1 && s1.equals(s2);
    }

    public static <K, V> String joinHashMap(HashMap<K, V> map, String interval, Func1R<V, String> func){
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for(Map.Entry<K, V> item : map.entrySet()){
            if(!isFirst){
                sb.append(interval);
            }
            sb.append(func.call(item.getValue()));
            isFirst = false;
        }
        return sb.toString();
    }
}
