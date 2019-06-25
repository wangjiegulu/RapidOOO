package com.wangjiegulu.rapidooo.library.compiler.util;

import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class TextUtil {
    public static  String firstCharUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String firstCharLower(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
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

    public static boolean isEmpty(Collection<?> collection){
        return null == collection || collection.isEmpty();
    }
    public static int size(Collection<?> collection){
        return null == collection ? 0 : collection.size();
    }

    public static <T> T pickFirst(Collection<T> collection, Func1R<T, Boolean> func){
        if(null == collection){
            return null;
        }
        for(T t : collection){
            if(func.call(t)){
                return t;
            }
        }
        return null;
    }

    public static <T> T[] addAll(final T[] array1, final T... array2) {
        if (array1 == null) {
            return array2;
        } else if (array2 == null) {
            return array1;
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        @SuppressWarnings("unchecked") // OK, because array is of type T
        final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        } catch (final ArrayStoreException ase) {
            // Check if problem was due to incompatible types
            /*
             * We do this here, rather than before the copy because:
             * - it would be a wasted check most of the time
             * - safer, in case check turns out to be too strict
             */
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of "
                        + type1.getName(), ase);
            }
            throw ase; // No, so rethrow original
        }
        return joinedArray;
    }
}
