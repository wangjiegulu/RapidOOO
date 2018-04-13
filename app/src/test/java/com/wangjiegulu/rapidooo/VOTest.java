package com.wangjiegulu.rapidooo;

import com.wangjiegulu.rapidooo.depmodule.bll.xbo.PetBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.User_BO;

import org.junit.Assert;
import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 13/04/2018.
 */
public class VOTest {
    @Test
    public void test01() throws Exception {
        User_BO user_bo = new User_BO();

        user_bo.setUserId(1234L);
        user_bo.setAge(28);
        user_bo.setUsername("Wang Jie");
        user_bo.setNickname("wangjiegulu");
        user_bo.setGender(1);

        PetBO petBO = new PetBO();
        petBO.setPetId(2000L);
        petBO.setPetName("Max");
        petBO.setDog(false);
        petBO.setCat(true);
        petBO.setClear(true);
        petBO.setOwnerUser(user_bo);


        PetVO petVO = PetVO.create(petBO);
        petVO.setPetName("Max1");
        petVO.setCat(false);
        petVO.getOwnerUser().setGenderDesc("female");
        petVO.getOwnerUser().setNickname("wangjieooo");

        PetBO newPetBo = petVO.toPetBO();
        Assert.assertEquals(2000L, newPetBo.getPetId().longValue());
        Assert.assertEquals("Max1", newPetBo.getPetName());
        Assert.assertFalse(newPetBo.isCat());
        Assert.assertEquals(0, newPetBo.getOwnerUser().getGender().intValue());
        Assert.assertEquals("wangjieooo", newPetBo.getOwnerUser().getNickname());

    }

}
