package com.wangjiegulu.rapidooo.vo.parcelable;

import com.wangjiegulu.rapidooo.AgeBinder;
import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.ParcelableBOGenerator;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetParentBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.User_BO;

/**
 * Generate VOs from BOs
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
@OOOs(suffix = ParcelableVOGenerator.VO_SUFFIX, fromSuffix = ParcelableBOGenerator.BO_SUFFIX, ooosPackages = {
        ParcelableVOGenerator.PACKAGE_BO
}, ooos = {
        @OOO(id = "pet_parent_vo_id", from = PetParentBO.class),
        @OOO(id = "user_vo_id", from = User_BO.class/*, suffix = ParcelableVOGenerator.VO_SUFFIX_USER*/,
                fromSuffix = ParcelableBOGenerator.BO_SUFFIX_USER,
                conversions = {
                        @OOOConversion(
                                targetFieldName = "genderDesc",
                                targetFieldType = String.class,
                                bindMethodName = "bindGender"
//                                inverseBindMethodName = "inverseBindGender"
                        ),
                        @OOOConversion(
                                targetFieldName = "ageDes",
                                targetFieldType = String.class,
                                bindMethodName = "bindAge",
                                bindMethodClass = AgeBinder.class
//                                inverseConversionMethodName = "inverseBindAge",
                        )
                }
        ),
        @OOO(targetSupperTypeId = "pet_parent_vo_id", from = PetBO.class,
                conversions = {
                        @OOOConversion(
                                targetFieldName = "ownerUser",
                                targetFieldTypeId = "user_vo_id"
                        )
                }
        )
})
public class ParcelableVOGenerator {
    public static final String VO_SUFFIX = "VO";
    //    public static final String VO_SUFFIX_USER = "_VO";
    public static final String PACKAGE_BO = "com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable";

    public static String bindGender(Integer gender) {
        if (null == gender) {
            return "unknown";
        }
        switch (gender) {
            case 0:
                return "female";
            case 1:
                return "male";
            default:
                return "unknown";
        }
    }

//    public static void inverseBindGender(String genderDesc, UserVO userVO) {
//        int age;
//        switch (genderDesc) {
//            case "male":
//                age = 1;
//                break;
//            case "female":
//                age = 0;
//                break;
//            default:
//                age = -1;
//                break;
//        }
//        userVO.setAge(age);
//    }

}
