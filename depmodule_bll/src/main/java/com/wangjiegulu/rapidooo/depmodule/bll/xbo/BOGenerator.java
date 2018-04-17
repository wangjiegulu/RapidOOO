package com.wangjiegulu.rapidooo.depmodule.bll.xbo;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.PetChild;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.User;

/**
 * Generate BOs from DOs
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
@OOOs(suffix = BOGenerator.BO_SUFFIX, ooos = {
        @OOO(from = PetChild.class),
        @OOO(id = "user_bo_id", from = User.class, suffix = BOGenerator.BO_SUFFIX_USER),
        @OOO(from = Pet.class, conversion = {
                @OOOConversion(
                        fieldName = "owner",
                        targetTypeId = "user_bo_id",
                        targetFieldName = "ownerUser",
//                        conversionMethodName = "conversionUserBO",
//                        inverseConversionMethodName = "inverseConversionUserBO",
                        replace = true
                )
        })
})
public class BOGenerator {
    public static final String BO_SUFFIX = "BO";
    public static final String BO_SUFFIX_USER = "_BO";
//    String PACKAGE_DO = "com.wangjiegulu.rapidooo.depmodule.dal._do";

    public static User_BO conversionUserBO(User user) {
        return User_BO.create(user);
    }

    public static User inverseConversionUserBO(User_BO owner) {
        return owner.toUser();
    }
}
