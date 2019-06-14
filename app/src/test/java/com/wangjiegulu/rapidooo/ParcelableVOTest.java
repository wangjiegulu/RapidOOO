package com.wangjiegulu.rapidooo;


import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.User_BO;
import com.wangjiegulu.rapidooo.vo.parcelable.PetVO;

import org.junit.Assert;
import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 13/04/2018.
 */
public class ParcelableVOTest {
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
        petBO.setFirstName("Max");
        petBO.setLastName("Will");
        petBO.setDog(false);
        petBO.setCat(true);
        petBO.setClear(true);
        petBO.setOwnerUser(user_bo);
        petBO.setChildName("petChildName");
        petBO.setLongBox(1234L);

        // petBO -> petVO
        PetVO petVO = PetVO.create(petBO);
        Assert.assertEquals(2000L, petVO.getPetId().longValue());
        Assert.assertEquals("Max", petVO.getFirstName());
        Assert.assertEquals("Will", petVO.getLastName());
        Assert.assertFalse(petVO.getDog());
        Assert.assertTrue(petVO.isCat());
        Assert.assertTrue(petVO.getClear());
        Assert.assertEquals("petChildName", petVO.getChildName());
        Assert.assertEquals(1234L, petVO.getLongBox().longValue());

        petVO.setFirstName("Max1");
        petVO.setCat(false);
        petVO.getOwnerUser().setGenderDesc("female");
        petVO.getOwnerUser().setNickname("wangjieooo");
        petVO.setChildName("petChildName2");

        // petVO -> new petBO
        PetBO newPetBo = petVO.toPetBO();
        Assert.assertEquals(2000L, newPetBo.getPetId().longValue());
        Assert.assertEquals("Max1", newPetBo.getFirstName());
        Assert.assertEquals("Will", newPetBo.getLastName());
        Assert.assertFalse(newPetBo.getDog());
        Assert.assertFalse(newPetBo.isCat());
        Assert.assertTrue(newPetBo.getClear());
        Assert.assertEquals("petChildName2", newPetBo.getChildName());
        Assert.assertEquals(1234L, newPetBo.getLongBox().longValue());

        Assert.assertEquals(0, newPetBo.getOwnerUser().getGender().intValue());
        Assert.assertEquals("wangjieooo", newPetBo.getOwnerUser().getNickname());

    }

}
