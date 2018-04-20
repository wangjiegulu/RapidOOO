package com.wangjiegulu.rapidooo;

import com.wangjiegulu.rapidooo.depmodule.bll.FakeInteractorImpl;
import com.wangjiegulu.rapidooo.depmodule.bll.xbo.parcelable.PetBO;
import com.wangjiegulu.rapidooo.vo.parcelable.PetVO;

import org.junit.Test;

import java.util.HashSet;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 19/04/2018.
 */
public class PerformanceTest {

    @Test
    public void test01() throws Exception {
        HashSet<String> objectHashSet = new HashSet<>();
        PetBO petBO1 = new FakeInteractorImpl().requestPet();
        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            PetVO petVO = PetVO.create(petBO1);
            petBO1 = petVO.toPetBO();
        }
        System.out.println("cost1: " + (System.currentTimeMillis() - start1) + " ms");


        PetBO petBO2 = new FakeInteractorImpl().requestPet();
        long start2 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            PetVO petVO = PetVO.create(petBO2);
            petBO2.release();
            petBO2 = petVO.toPetBO();
        }
        System.out.println("cost2: " + (System.currentTimeMillis() - start2) + " ms");


    }

}
