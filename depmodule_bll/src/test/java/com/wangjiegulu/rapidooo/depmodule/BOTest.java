package com.wangjiegulu.rapidooo.depmodule;


import com.wangjiegulu.rapidooo.depmodule.bll.xbo.PetBO;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.User;

import org.junit.Assert;
import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 16/04/2018.
 */
public class BOTest {
    @Test
    public void test01() throws Exception {
        User user = new User();

        user.setUserId(1234L);
        user.setAge(28);
        user.setUsername("Wang Jie");
        user.setNickname("wangjiegulu");
        user.setGender(1);

        Pet pet = new Pet();
        pet.setPetId(2000L);
        pet.setPetName("Max");
        pet.setDog(false);
        pet.setCat(true);
        pet.setClear(true);
        pet.setOwner(user);

        // pet -> petBO
        PetBO petBO = PetBO.create(pet);

        Assert.assertEquals(2000L, petBO.getPetId().longValue());
        Assert.assertEquals("Max", petBO.getPetName());
        Assert.assertFalse(petBO.getDog());
        Assert.assertTrue(petBO.isCat());
        Assert.assertTrue(petBO.getClear());

        petBO.setPetName("Max1");
        petBO.setCat(false);
        petBO.getOwnerUser().setNickname("wangjieooo");

        // petBO -> new pet
        Pet newPet = petBO.toPet();
        Assert.assertEquals(2000L, newPet.getPetId().longValue());
        Assert.assertEquals("Max1", newPet.getPetName());
        Assert.assertFalse(newPet.getDog());
        Assert.assertFalse(newPet.isCat());
        Assert.assertTrue(newPet.getClear());

        Assert.assertEquals(1, newPet.getOwner().getGender().intValue());
        Assert.assertEquals("wangjieooo", newPet.getOwner().getNickname());



    }
}
