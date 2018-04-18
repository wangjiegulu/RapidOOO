package com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable;

import android.support.v4.util.Pools;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOPool;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.PetParent;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.User;

/**
 * Generate BOs from DOs
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
@OOOs(suffix = ParcelableBOGenerator.BO_SUFFIX, ooos = {
        @OOO(id = "pet_parent_bo_id", from = PetParent.class),
        @OOO(id = "user_bo_id", from = User.class, suffix = ParcelableBOGenerator.BO_SUFFIX_USER),
        @OOO(from = Pet.class, targetSupperTypeId = "pet_parent_bo_id", conversion = {
                @OOOConversion(
                        fieldName = "owner",
                        targetTypeId = "user_bo_id",
                        targetFieldName = "ownerUser",
//                        conversionMethodName = "conversionUserBO",
//                        inverseConversionMethodName = "inverseConversionUserBO",
                        replace = true
                )
        }, pool = @OOOPool(acquireMethod = "acquirePetBO", releaseMethod = "releasePetBO"))
})
public class ParcelableBOGenerator {
    public static final String BO_SUFFIX = "BO";
    public static final String BO_SUFFIX_USER = "_BO";

    public static User_BO conversionUserBO(User user) {
        return User_BO.create(user);
    }

    public static User inverseConversionUserBO(User_BO owner) {
        return owner.toUser();
    }


    private static Pools.Pool<PetBO> petBOPool = new Pools.SimplePool<>(3);
    public static PetBO acquirePetBO() {
        PetBO petBO = petBOPool.acquire();
        return null == petBO ? new PetBO() : petBO;
    }

    public static void releasePetBO(PetBO petBO) {
        petBOPool.release(petBO);
    }

}
