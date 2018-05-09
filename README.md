# RapidOOO

[![Build Status](https://travis-ci.org/wangjiegulu/RapidOOO.svg?branch=master)](https://travis-ci.org/wangjiegulu/RapidOOO) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.wangjiegulu/rapidooo-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.wangjiegulu/rapidooo-api) [![](https://img.shields.io/badge/license-Apache 2-orange.svg)](http://www.apache.org/licenses/LICENSE-2.0) ![API](https://img.shields.io/badge/API-1%2B-brightgreen.svg?style=flat)

[![](https://img.shields.io/badge/blog-Wang Jie-228377.svg)](https://blog.wangjiegulu.com) [![](https://img.shields.io/badge/twitter-@wangjiegulu-blue.svg)](https://twitter.com/wangjiegulu)

Android POJO Converter:Generate scalable and bindable domain objects java class file automatically in compile time.

[中文版本](README_zh.md)

## Why RapidOOO ?

We always transform data between different layer in Domain-Driven Design, such as  `VO`, `PO`, `DO`, `DTO`, `BO`. The same situation is often encountered in the development of Android, For example,  [UserModelDataMapper::transform](https://github.com/android10/Android-CleanArchitecture/blob/master/presentation/src/main/java/com/fernandocejas/android10/sample/presentation/mapper/UserModelDataMapper.java#L42), [UserEntityDataMapper::transform](https://github.com/android10/Android-CleanArchitecture/blob/master/data/src/main/java/com/fernandocejas/android10/sample/data/entity/mapper/UserEntityDataMapper.java#L42) in [Android-CleanArchitecture](https://github.com/android10/Android-CleanArchitecture). The process of manually copying and converting is not only cumbersome, but also has a relatively large risk of errors. It also increases maintenance costs when adding or deleting fields. [Dozer](http://dozer.sourceforge.net/documentation/about.html) can be a good solution to this problem, but it may not be applicable on Android.



**RapidOOO Can Do:**

1. Java classes (such as `UserVO`, `UserBO`, etc.) can be automatically generated for non-reflection in compile time for the specified original POJO.
2. You can add configuration to the generated POJO class, add new fields (such as `gender` in User to create a `genderDesc` field in the generated POJO (UserVO), and coexist with the original `gender` class and data binding)
3. You can perform special conversions for fields by specifying `conversionMethodName`, `inverseConversionMethodName`. similar to `@BindingMethod` in `Databinding`.
4. Chain POJO generation, such as generating `UserDO` from `User`, `UserBO` from `UserDO`, `UserVO` from `UserBO`...
5. The conversion method `UserBo.create(User user)`, `userBo.toUser()` in generated class will be automatically generate.
6. Support POJO `extend`.
7. Support Object Pool(Such as `android.support.v4.util.Pools`).


## How to use ?

Gradle [Check Newest Version](http://search.maven.org/#search%7Cga%7C1%7Crapidooo)

```groovy
implementation "com.github.wangjiegulu:rapidooo-api:x.x.x"
annotationProcessor "com.github.wangjiegulu:rapidooo-compiler:x.x.x"
```

Here are two examples:

**User POJO:**

```java
public class User implements Serializable {
    private Long userId;
    private String username;
    private String nickname;
    private Integer age;
    private Integer gender;
    // getter setter
}
```

**Pet POJO:**

```java
public class Pet {
    private Long petId;
    private String petName;
    private boolean isCat;
    private boolean delete;
    private Boolean isDog;
    private Boolean clear;
    private User owner;
    // getter  settter
}
```

### POJO Convert to BO

Create `BOGenerator` class, Configure the following annotation:

```java
@OOOs(suffix = BOGenerator.BO_SUFFIX, ooos = {
        @OOO(id = "user_bo_id", from = User.class, suffix = BOGenerator.BO_SUFFIX_USER),
        @OOO(from = Pet.class, conversion = {
                @OOOConversion(
                        fieldName = "owner",
                        targetTypeId = "user_bo_id",
                        targetFieldName = "ownerUser",
                        replace = true
                )
        })
})
public class BOGenerator {
    public static final String BO_SUFFIX = "BO";
    public static final String BO_SUFFIX_USER = "_BO";
}
```

Using the `@OOOs` annotation to configure conversions. The `@OOO` annotations explicitly specify which classes need to be converted.

```java
@OOO(id = "user_bo_id", from = User.class, suffix = BOGenerator.BO_SUFFIX_USER)
```

The above represents a class conversion:

- **id:**Represent the id of this transformation, can be an arbitrary string (unique), and the default does not set the id.
- **from:**required. Represent the conversion source. from the `User` conversion.
- **suffix:**Represent the name of the generated POJO class suffix, here is `_BO`, so the generated class name is `User_BO`, by default it uses `suffix` in `@OOOs`.

```java
@OOO(from = Pet.class, conversion = {
      @OOOConversion(
              fieldName = "owner",
              targetTypeId = "user_bo_id",
              targetFieldName = "ownerUser",
              replace = true
      )
})
```

The above also represents a class conversion, but a field can be added with `@OOOConversion`:

- **fieldName:**Specifying a new field is derived from which field of the source POJO is converted.
- **targetTypeId:**Used to specify the type id of the new field. It needs to be consistent with the `id` specified by the other `@OOO`; it is also possible to specify the Class type via `targetType`.
- **targetFieldName:**Specify the name of the new field, which can be arbitrary.
- **replace:**Whether the new field replaces the original field (**fieldName**), if false, coexist.


Following code will be automatically generated in compile time:

```java
public class User_BO implements Serializable {
  private Long userId;
  private String username;
  private String nickname;
  private Integer age;
  private Integer gender;

  // getter setter

  public static User_BO create(User user) {
    User_BO user_BO = new User_BO();
    user_BO.userId = user.getUserId();
    user_BO.username = user.getUsername();
    user_BO.nickname = user.getNickname();
    user_BO.age = user.getAge();
    user_BO.gender = user.getGender();
    return user_BO;
  }

  public User toUser() {
    User user = new User();
    user.setUserId(userId);
    user.setUsername(username);
    user.setNickname(nickname);
    user.setAge(age);
    user.setGender(gender);
    return user;
  }

}
```

```java
public class PetBO {
  private Long petId;
  private String petName;
  private boolean isCat;
  private boolean delete;
  private Boolean isDog;
  private Boolean clear;
  private User_BO ownerUser;

  // getter setter

  public static PetBO create(Pet pet) {
    PetBO petBO = new PetBO();
    petBO.petId = pet.getPetId();
    petBO.petName = pet.getPetName();
    petBO.isCat = pet.isCat();
    petBO.delete = pet.isDelete();
    petBO.isDog = pet.getDog();
    petBO.clear = pet.getClear();
    petBO.ownerUser = User_BO.create(pet.getOwner());
    return petBO;
  }

  public Pet toPet() {
    Pet pet = new Pet();
    pet.setPetId(petId);
    pet.setPetName(petName);
    pet.setCat(isCat);
    pet.setDelete(delete);
    pet.setDog(isDog);
    pet.setClear(clear);
    pet.setOwner(ownerUser.toUser());
    return pet;
  }
}
```

### BO Convert to VO

Create `VOGenerator` as follows:

```java
@OOOs(suffix = VOGenerator.VO_SUFFIX, fromSuffix = BOGenerator.BO_SUFFIX, ooosPackages = {
        VOGenerator.PACKAGE_BO
}, ooos = {
        @OOO(id = "user_vo_id", from = User_BO.class),
        @OOO(from = User_BO.class/*, suffix = VOGenerator.VO_SUFFIX_USER*/,
                fromSuffix = BOGenerator.BO_SUFFIX_USER,
                conversion = {
                        @OOOConversion(
                                fieldName = "gender",
                                targetFieldName = "genderDesc",
                                targetType = String.class,
                                conversionMethodName = "conversionGender",
                                inverseConversionMethodName = "inverseConversionGender",
                                replace = false
                        ),
                        @OOOConversion(
                                fieldName = "age",
                                targetFieldName = "ageDes",
                                targetType = String.class,
                                conversionMethodName = "conversionAge",
                                conversionMethodClass = AgeConversion.class,
                                replace = true
                        )
                }
        ),
        @OOO(from = PetBO.class,
                conversion = {
                        @OOOConversion(
                                fieldName = "ownerUser",
                                targetFieldName = "ownerUser",
                                targetTypeId = "user_vo_id",
                                replace = true
                        )
                }
        )
})
public class VOGenerator {
    public static final String VO_SUFFIX = "VO";
    //    public static final String VO_SUFFIX_USER = "_VO";
    public static final String PACKAGE_BO = "com.wangjiegulu.rapidooo.depmodule.bll.xbo";

    public static String conversionGender(Integer gender) {
        if (null == gender) {
            return "unknown";
        }
        switch (gender) {
            case 0:
                return "female";
            case 1:
                return "male";
            default:
                return "unknown";
        }
    }

    public static Integer inverseConversionGender(String genderDesc) {
        switch (genderDesc) {
            case "male":
                return 1;
            case "female":
                return 0;
            default:
                return -1;
        }
    }

}
```

The `@OOOs` annotation is also used to specify the class to be generated, but here `ooosPackages` is used to specify which classes under the package need to be converted.

The conversion source is generated above:`User_BO` and `PetBO`, and the generated classes are `UserVO` and `PetVO`.

Extended two fields in `UserVO`:

```java
@OOOConversion(
       fieldName = "gender",
       targetFieldName = "genderDesc",
       targetType = String.class,
       conversionMethodName = "conversionGender",
       inverseConversionMethodName = "inverseConversionGender",
       replace = false
)
```

Extend `genderDesc` (used for display on View) from the `gender` field of the conversion source, of type `String`, and `replace = false`(`gender` coexists with `genderDesc`):

- **conversionMethodName:**Specify the conversion method from `gender` to `genderDesc`. The default is not set.
- **inverseConversionMethodName:**Specify the inverse conversion method from `genderDesc` to `gender`. Default is not set.

> **NOTE:** When use `conversionMethodName` and `inverseConversionMethodName` to specify method names, the method signature must satisfy one of the following:
> - `public static [Conversion target type] conversionXxx([Conversion source field type] param)`
> -  `public static [Conversion target type] conversionXxx([Conversion source class type] param1, [Conversion source field type] param2)`
> For example, the transformation method between `gender` and `genderDesc` on top:
> - `public static String conversionGender(UserVO userVO, Integer gender)`
> - `public static Integer inverseConversionGender(String genderDesc)`

By setting the above two methods, the `gender` and `genderDesc` fields will be bind to each other, change one of the fields, and the other field will automatically change.

```java
@OOOConversion(
       fieldName = "age",
       targetFieldName = "ageDes",
       targetType = String.class,
       conversionMethodName = "conversionAge",
       conversionMethodClass = AgeConversion.class,
       replace = true
)
```

`UserVO` also extends an `ageDesc` field (replaces the `age` field, does not coexist) from the `age` of the conversion source and specifies `conversionMethodName`, but the conversion method is not in the `VOGenerator` class, and it is in the `AgeConversion` class, so you need to explicitly specify `conversionMethodClass`.

- **conversionMethodClass:**The class where the conversion method is , which is not set by default, in the current `Generator` class.

In addition `PetVO` extends ʻownerUser` field.

The final compiled code is as follows:

```java
public class UserVO implements Serializable {
  private Long userId;
  private String username;
  private String nickname;
  private String ageDes;
  private Integer gender;
  private String genderDesc;

  // getter setter

  public void setGender(Integer gender) {
    this.gender = gender;
    this.genderDesc = VOGenerator.conversionGender(gender);
  }

  public void setGenderDesc(String genderDesc) {
    this.genderDesc = genderDesc;
    this.gender = VOGenerator.inverseConversionGender(genderDesc);
  }

  public static UserVO create(User_BO user_BO) {
    UserVO userVO = new UserVO();
    userVO.userId = user_BO.getUserId();
    userVO.username = user_BO.getUsername();
    userVO.nickname = user_BO.getNickname();
    userVO.ageDes = AgeConversion.conversionAge(user_BO.getAge());
    userVO.gender = user_BO.getGender();
    userVO.genderDesc = VOGenerator.conversionGender(user_BO.getGender());
    return userVO;
  }

  public User_BO toUser_BO() {
    User_BO user_BO = new User_BO();
    user_BO.setUserId(userId);
    user_BO.setUsername(username);
    user_BO.setNickname(nickname);
    // Loss field:age, recommend to use `inverseConversionMethodName`.
    user_BO.setGender(gender);
    return user_BO;
  }
}
```

> **NOTE:** Above `User_BO`, because of the `age` field is `replace`, and only `conversionMethodName` is set, `inverseConversionMethodName` is not set, so the `age` value is lost when the `toUser_BO()` method is inverse converted. It is recommended to use `inverseConversionMethodName`.

```java
public class PetVO {
  private Long petId;
  private String petName;
  private boolean isCat;
  private boolean delete;
  private Boolean isDog;
  private Boolean clear;
  private UserVO ownerUser;

  // getter setter

  public static PetVO create(PetBO petBO) {
    PetVO petVO = new PetVO();
    petVO.petId = petBO.getPetId();
    petVO.petName = petBO.getPetName();
    petVO.isCat = petBO.isCat();
    petVO.delete = petBO.isDelete();
    petVO.isDog = petBO.getDog();
    petVO.clear = petBO.getClear();
    petVO.ownerUser = UserVO.create(petBO.getOwnerUser());
    return petVO;
  }

  public PetBO toPetBO() {
    PetBO petBO = new PetBO();
    petBO.setPetId(petId);
    petBO.setPetName(petName);
    petBO.setCat(isCat);
    petBO.setDelete(delete);
    petBO.setDog(isDog);
    petBO.setClear(clear);
    petBO.setOwnerUser(ownerUser.toUser_BO());
    return petBO;
  }
}
```


License
=======

```
Copyright 2018 Wang Jie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


