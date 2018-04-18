package com.wangjiegulu.rapidooo.depmodule;

import com.wangjiegulu.rapidooo.depmodule.bll.xbo.simple.AnimalBO;
import com.wangjiegulu.rapidooo.depmodule.dal.xdo.simple.Animal;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 18/04/2018.
 */
public class SimpleBOTest {
    @Test
    public void test01() throws Exception {
        Animal animal = new Animal();
        animal.setName("animal_name");
        animal.setMammal(true);
        animal.setWeight(100.0f);
        animal.setAnimalType("type1");

        AnimalBO animalBO = AnimalBO.create(animal);
        Assert.assertEquals("animal_name", animalBO.getName());
        Assert.assertTrue(animalBO.isMammal());
        Assert.assertEquals(100.0f, animalBO.getWeight());
        Assert.assertEquals("type1", animalBO.getAnimalType());

        animalBO.setName("animal_name_2");
        animalBO.setMammal(false);
        animalBO.setWeight(101.0f);
        animalBO.setAnimalType("type2");

        Animal newAnimal = animalBO.toAnimal();
        Assert.assertEquals("animal_name_2", newAnimal.getName());
        Assert.assertFalse(newAnimal.isMammal());
        Assert.assertEquals(101.0f, newAnimal.getWeight());
        Assert.assertEquals("type2", newAnimal.getAnimalType());


    }
}
