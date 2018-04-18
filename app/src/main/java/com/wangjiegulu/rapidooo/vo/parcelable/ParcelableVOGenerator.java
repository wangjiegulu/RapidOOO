package com.wangjiegulu.rapidooo.vo.parcelable;

import com.wangjiegulu.rapidooo.AgeConversion;
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
                conversion = {
                        @OOOConversion(
                                fieldName = "gender",
                                targetFieldName = "genderDesc",
                                targetType = String.class,
                                conversionMethodName = "conversionGender",
                                inverseConversionMethodName = "inverseConversionGender",
                                replace = false
                        ),
                        @OOOConversion(
                                fieldName = "age",
                                targetFieldName = "ageDes",
                                targetType = String.class,
                                conversionMethodName = "conversionAge",
                                conversionMethodClass = AgeConversion.class,
//                                inverseConversionMethodName = "inverseConversionAge",
                                replace = true
                        )
                }
        ),
        @OOO(targetSupperTypeId = "pet_parent_vo_id", from = PetBO.class,
                conversion = {
                        @OOOConversion(
                                fieldName = "ownerUser",
                                targetFieldName = "ownerUser",
                                targetTypeId = "user_vo_id",
                                replace = true
                        )
                }
        )
})
public class ParcelableVOGenerator {
    public static final String VO_SUFFIX = "VO";
    //    public static final String VO_SUFFIX_USER = "_VO";
    public static final String PACKAGE_BO = "com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable";

    public static String conversionGender(Integer gender) {
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

    public static Integer inverseConversionGender(String genderDesc) {
        switch (genderDesc) {
            case "male":
                return 1;
            case "female":
                return 0;
            default:
                return -1;
        }
    }

}
