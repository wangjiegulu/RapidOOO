package com.wangjiegulu.rapidooo.library.compiler.part.statement.contact;

import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public interface ParcelableEntry {
    boolean isParcelable();
    String fieldName();
    OOOTypeEntry fieldTypeEntry();
//    TypeName fieldType();
}
