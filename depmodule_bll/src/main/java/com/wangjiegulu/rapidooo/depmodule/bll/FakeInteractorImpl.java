package com.wangjiegulu.rapidooo.depmodule.bll;

import com.wangjiegulu.rapidooo.depmodule.bll.xbo.PetBO;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.User_BO;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.Pet;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.User;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 10/04/2018.
 */
public class FakeInteractorImpl {
    public PetBO requestPet() {
        // TODO: 10/04/2018 wangjie
        Pet pet = new Pet();
        pet.setPetId(2001L);
        pet.setPetName("Max");

        User user = new User();
        user.setUserId(1001L);
        user.setAge(18);
        user.setUsername("wangjiegulu");
        user.setNickname("Wang Jie");
        user.setGender(1);

        User_BO userBO = User_BO.create(user);

        PetBO petBO = PetBO.create(pet);
//        petDTO_.setOwner(userDTO_);

        return petBO;
    }
}
