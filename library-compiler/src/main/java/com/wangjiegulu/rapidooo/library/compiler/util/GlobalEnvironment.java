package com.wangjiegulu.rapidooo.library.compiler.util;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 3/18/16.
 */
public class GlobalEnvironment {
    private static ProcessingEnvironment processingEnv;

    public static void init(ProcessingEnvironment processingEnv){
        GlobalEnvironment.processingEnv = processingEnv;
    }
    public static ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    public static Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
}
