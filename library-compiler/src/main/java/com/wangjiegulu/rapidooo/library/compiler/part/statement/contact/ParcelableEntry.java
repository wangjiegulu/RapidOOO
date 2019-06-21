package com.wangjiegulu.rapidooo.library.compiler.part.statement.contact;

import com.squareup.javapoet.TypeName;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public interface ParcelableEntry {
    boolean isParcelable();
    String fieldName();
    TypeName fieldType();
}
