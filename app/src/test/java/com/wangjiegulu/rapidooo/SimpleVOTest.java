package com.wangjiegulu.rapidooo;

import com.wangjiegulu.rapidooo.depmodule.bll.xbo.simple.AnimalBO;
import com.wangjiegulu.rapidooo.vo.simple.AnimalVO;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 18/04/2018.
 */
public class SimpleVOTest {
    @Test
    public void test01() throws Exception {
        AnimalBO animalBO = new AnimalBO();
        animalBO.setName("animal_name");
        animalBO.setMammal(true);
        animalBO.setWeight(100.0f);
        animalBO.setAnimalType("type1");

        AnimalVO animalVO = AnimalVO.create(animalBO);
        Assert.assertEquals("animal_name", animalVO.getName());
        Assert.assertTrue(animalVO.isMammal());
        Assert.assertEquals(100.0f, animalVO.getWeight());
        Assert.assertEquals("type1", animalVO.getAnimalType());

        animalVO.setName("animal_name_2");
        animalVO.setMammal(false);
        animalVO.setWeight(101.0f);
        animalVO.setAnimalType("type2");

        AnimalBO newAnimalBO = animalVO.toAnimalBO();
        Assert.assertEquals("animal_name_2", newAnimalBO.getName());
        Assert.assertFalse(newAnimalBO.isMammal());
        Assert.assertEquals(101.0f, newAnimalBO.getWeight());
        Assert.assertEquals("type2", newAnimalBO.getAnimalType());


    }
}
