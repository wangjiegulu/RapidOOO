package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public interface PartBrew {
    void brew(OOOEntry oooEntry, TypeSpec.Builder result);
}
