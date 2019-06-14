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
 * Generate BOs from DOs Author: wangjie Email: tiantian.china.2@gmail.com Date: 10/04/2018.
 */
@OOOs(suffix = ParcelableBOGenerator.BO_SUFFIX, ooos = {
        @OOO(id = "pet_parent_bo_id", from = PetParent.class),
        @OOO(id = "user_bo_id", from = User.class, suffix = ParcelableBOGenerator.BO_SUFFIX_USER),
        @OOO(from = Pet.class, targetSupperTypeId = "pet_parent_bo_id", suffix = "2BO", excludes = {"owner"}),
        @OOO(from = Pet.class, targetSupperTypeId = "pet_parent_bo_id", excludes = {"owner"},
                conversions = {
                        @OOOConversion(
                                targetFieldName = "ownerUser",
                                targetFieldTypeId = "user_bo_id",

                                attachFieldName = "owner"
                        ),
                        @OOOConversion(
                                targetFieldName = "fullName",
                                targetFieldType = String.class,

                                bindMethodName = "bindPetFullName",
                                inverseBindMethodName = "inverseBindPetFullName"
                        )
                },
                pool = @OOOPool(acquireMethod = "acquirePetBO", releaseMethod = "releasePetBO")
        )
})
public class ParcelableBOGenerator {
    public static final String BO_SUFFIX = "BO";
    public static final String BO_SUFFIX_USER = "_BO";


    public static String bindPetFullName(String lastName, boolean isCat, PetBO self, String firstName) {
        return firstName + " " + lastName;
    }

    public static void inverseBindPetFullName(String fullName, PetBO self) {
        String[] names = fullName.split(" ");
        if (names.length == 2) {
            self.setFirstName(names[0]);
            self.setLastName(names[1]);
        }
    }

    public static User_BO conversionOwnerUser(User user) {
        return User_BO.create(user);
    }

    public static void inverseConversionOwnerUser(User_BO ownerUser, Pet pet) {
        pet.setOwner(ownerUser.toUser());
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
