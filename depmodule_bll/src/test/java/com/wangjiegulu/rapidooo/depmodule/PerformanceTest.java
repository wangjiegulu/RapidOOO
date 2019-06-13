package com.wangjiegulu.rapidooo.depmodule;

import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetBO;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.parcelable.User;

import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 19/04/2018.
 */
public class PerformanceTest {

    @Test
    public void test01() throws Exception {
        Pet pet = createPet();

//        HashSet<String> objectHashSet1 = new HashSet<>();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            PetBO petBO = PetBO.create(pet);
//            objectHashSet1.add(String.valueOf(petBO.hashCode()));
        }
        System.out.println("cost1: " + (System.currentTimeMillis() - start1) + " ms");
//        System.out.println("objectHashSet1 size: " + objectHashSet1.size());

//        HashSet<String> objectHashSet2 = new HashSet<>();
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            PetBO petBO = PetBO.create(pet);
//            objectHashSet2.add(String.valueOf(petBO.hashCode()));
            petBO.release();
        }
        System.out.println("cost2: " + (System.currentTimeMillis() - start2) + " ms");
//        System.out.println("objectHashSet2 size: " + objectHashSet2.size());


    }

    public Pet createPet() {
        // TODO: 10/04/2018 wangjie
        Pet pet = new Pet();
        pet.setPetId(2001L);
        pet.setFirstName("Max");

        User user = new User();
        user.setUserId(1001L);
        user.setAge(18);
        user.setUsername("wangjiegulu");
        user.setNickname("Wang Jie");
        user.setGender(1);

        pet.setOwner(user);

        return pet;
    }

}
