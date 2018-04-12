package com.wangjiegulu.rapidooo;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class AgeConversion {

    public static String conversionAge(UserVO userVO, Integer age) {
        if (null == age || age < 0) {
            return "unknown";
        }
        return age + " years old";
    }

}
