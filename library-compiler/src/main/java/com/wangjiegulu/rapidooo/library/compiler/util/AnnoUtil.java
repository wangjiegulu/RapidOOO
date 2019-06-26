package com.wangjiegulu.rapidooo.library.compiler.util;

import com.wangjiegulu.rapidooo.api.OOOConstants;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.api.func.Func0R;

import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class AnnoUtil {
    public static boolean oooParamIsNotSet(String stuff) {
        return OOOConstants.NOT_SET.equals(stuff);
    }

    public static TypeMirror getType(Func0R<Object> func){
        try {
            func.call();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        throw new RapidOOOCompileException("AnnoUtil::getType error");
    }

}
