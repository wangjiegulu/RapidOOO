package com.wangjiegulu.rapidooo.library.compiler.control;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-26.
 */
public class ControlDelegateUtil {
    public final static List<ControlDelegateSpec> controlDelegateSpecs = new ArrayList<>();
    static {
        controlDelegateSpecs.add(new LazyControlDelegateSpec());
        controlDelegateSpecs.add(new NormalControlDelegateSpec());
    }
}
