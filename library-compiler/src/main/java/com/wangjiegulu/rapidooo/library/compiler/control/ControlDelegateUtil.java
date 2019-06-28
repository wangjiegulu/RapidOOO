package com.wangjiegulu.rapidooo.library.compiler.control;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.control.OOOLazyControlDelegate;
import com.wangjiegulu.rapidooo.api.control.OOOLazySyncControlDelegate;
import com.wangjiegulu.rapidooo.api.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class ControlDelegateUtil {
    /**
     * K: controlDelegateTypeName V: matched
     */
    public final static HashMap<Func1R<TypeName, Boolean>, Func1R<OOOTypeEntry, ControlDelegateSpec>> controlDelegateSpecs = new LinkedHashMap<>();

    static {
        // lazy
        controlDelegateSpecs.put(
                controlDelegateTypeName ->
                        ElementUtil.isSameType(controlDelegateTypeName, OOOLazyControlDelegate.class)
                                ||
                                ElementUtil.isSameType(controlDelegateTypeName, OOOLazySyncControlDelegate.class)
                , LazyControlDelegateSpec::new);
        // normal
        controlDelegateSpecs.put(controlDelegateTypeName -> true, NormalControlDelegateSpec::new);

    }

}
