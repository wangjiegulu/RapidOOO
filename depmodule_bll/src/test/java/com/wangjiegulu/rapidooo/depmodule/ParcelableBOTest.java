package com.wangjiegulu.rapidooo.depmodule;


import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetBO;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.User;

import org.junit.Assert;
import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 16/04/2018.
 */
public class ParcelableBOTest {
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
        pet.setFirstName("Max");
        pet.setLastName("Will");
        pet.setDog(false);
        pet.setCat(true);
        pet.setClear(true);
        pet.setOwner(user);
        pet.setChildName("petChildName");
        pet.setLongBox(1234L);

        // pet -> petBO
        PetBO petBO = PetBO.create(pet);

        Assert.assertEquals(2000L, petBO.getPetId().longValue());
//        Assert.assertEquals("Max", petBO.getPetName());
        Assert.assertEquals("Max", petBO.getFirstName());
        Assert.assertEquals("Will", petBO.getLastName());
        Assert.assertFalse(petBO.getDog());
        Assert.assertTrue(petBO.isCat());
        Assert.assertTrue(petBO.getClear());
        Assert.assertEquals("petChildName", petBO.getChildName());
        Assert.assertEquals(1234L, petBO.getLongBox().longValue());

        petBO.setFirstName("Max1");
        petBO.setCat(false);
        petBO.getOwnerUser().setNickname("wangjieooo");
        petBO.setChildName("petChildName2");

        // petBO -> new pet
        Pet newPet = petBO.toPet();
        Assert.assertEquals(2000L, newPet.getPetId().longValue());
        Assert.assertEquals("Max1", newPet.getFirstName());
        Assert.assertEquals("Will", newPet.getLastName());
        Assert.assertFalse(newPet.getDog());
        Assert.assertFalse(newPet.isCat());
        Assert.assertTrue(newPet.getClear());
        Assert.assertEquals("petChildName2", newPet.getChildName());
        Assert.assertEquals(1234L, newPet.getLongBox().longValue());

        Assert.assertEquals(1, newPet.getOwner().getGender().intValue());
        Assert.assertEquals("wangjieooo", newPet.getOwner().getNickname());
    }


}
