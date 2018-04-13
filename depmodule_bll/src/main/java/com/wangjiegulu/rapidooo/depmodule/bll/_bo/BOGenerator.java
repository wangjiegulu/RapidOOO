package com.wangjiegulu.rapidooo.depmodule.bll._bo;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.dal._do.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal._do.User;

/**
 * Generate BOs from DOs
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
@OOOs(suffix = BOGenerator.BO_SUFFIX, ooos = {
        @OOO(from = User.class, suffix = BOGenerator.BO_SUFFIX_USER),
        @OOO(from = Pet.class)
})
public interface BOGenerator {
    String BO_SUFFIX = "BO";
    String BO_SUFFIX_USER = "_BO";
//    String PACKAGE_DO = "com.wangjiegulu.rapidooo.depmodule.dal._do";
}
