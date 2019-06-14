package com.wangjiegulu.rapidooo;

import com.wangjiegulu.rapidooo.vo.parcelable.UserVO;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class AgeBinder {

    public static String bindAge(Integer age) {
        if (null == age || age < 0) {
            return "unknown";
        }
        return age + " years old";
    }

    public static void inverseBindAge(String ageDesc, UserVO self) {
        Integer age;
        if (null == ageDesc) {
            age = -1;
        }else{
            age = Integer.valueOf(ageDesc.split(" ")[0]);
        }
        self.setAge(age);
    }

}
