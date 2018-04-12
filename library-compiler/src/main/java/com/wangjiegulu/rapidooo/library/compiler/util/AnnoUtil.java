package com.wangjiegulu.rapidooo.library.compiler.util;

import com.wangjiegulu.rapidooo.api.OOOConstants;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class AnnoUtil {
    public static boolean oooParamIsNotSet(String stuff) {
        return OOOConstants.NOT_SET.equals(stuff);
    }
}
