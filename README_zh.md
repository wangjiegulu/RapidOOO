# RapidOOO

Android POJO 转换器：根据 POJO 类编译时自动生成支持扩展互相绑定的领域对象。

## 为什么使用 RapidOOO？

我们在领域驱动设计中经常会在不同层级之间传递数据，例如 `VO`, `PO`, `DO`, `DTO`, `BO`等。Android 的开发中也经常会遇到这些情况，比如在 [Android-CleanArchitecture](https://github.com/android10/Android-CleanArchitecture) 的 [UserModelDataMapper::transform](https://github.com/android10/Android-CleanArchitecture/blob/master/presentation/src/main/java/com/fernandocejas/android10/sample/presentation/mapper/UserModelDataMapper.java#L42), [UserEntityDataMapper::transform](https://github.com/android10/Android-CleanArchitecture/blob/master/data/src/main/java/com/fernandocejas/android10/sample/data/entity/mapper/UserEntityDataMapper.java#L42) 等。手工地进行拷贝转换的过程不但繁琐，而且错误的风险比较大，在新增、删除字段时也增加了维护的成本。[Dozer](http://dozer.sourceforge.net/documentation/about.html) 可以很好地解决这个问题，但是在 Android 上可能就不太适用了。

**RapidOOO 可以做到：**

1. 在编译时针对指定的初始 POJO，可以自动生成 Java 类（比如 `UserVO`, `UserBO` 等），非反射。
2. 可以在生成的 POJO 类中增加配置，添加新的字段（比如通过 User 中的 `gender` 在生成的 POJO（UserVO） 中扩展出一个 `genderDesc` 字段，并且与原来的 `gender` 类共存并进行双向绑定）
3. 字段进行转换时可以通过指定 `conversionMethodName`, `inverseConversionMethodName` 等方法来进行特殊的转换，类似 `Databinding` 中的 `@BindingMethod`。
4. 链式的 POJO 生成，如从 `User` 生成 `UserDO`, 从 `UserDO` 生成 `UserBO`, 从 `UserBO` 生成 `UserVO`...
5. 生成类中自动生成转换方法 `UserBo.create(User user)`, `userBo.toUser()`。

**后续 feature：**

- POJO 继承
- 配置对象池

## 怎么使用？

> 尚未上传到 Maven Central，具体的依赖方式稍后

以下通过两个例子来说明：

**User POJO：**

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

**Pet POJO：**

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

### POJO 转换为 BO

创建 `BOGenerator` 类，配置以下注解：

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

这里使用 `@OOOs` 注解来进行转换的配置，通过 `@OOO` 注解来显示地指定需要转换成哪些类。

```java
@OOO(id = "user_bo_id", from = User.class, suffix = BOGenerator.BO_SUFFIX_USER)
```

以上表示一个类的转换：

- **id：**表示本地转换的 id，可以为任意字符串（需唯一），默认不设置 id。
- **from：**表示转换源，从 `User` 转换，必填。
- **suffix：**表示生成的 POJO 类的名字后缀，这里是 `_BO`，所以生成的类名为 `User_BO`，默认使用 `@OOOs` 中的 `suffix`。

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

以上也表示一个类的转换，但是可以通过 `@OOOConversion` 来新增一个字段：

- **fieldName：**指定新的字段是从转换源 POJO 的哪个字段派生出来的
- **targetTypeId：**用来指定新的字段的类型id，需要与其它的 `@OOO` 指定的 `id` 一致；也可以通过 `targetType` 来指定 Class 类型。
- **targetFieldName：**指定新字段的名字，可以任意。
- **replace：**新的字段是否替换原来的字段（**fieldName**），如果 false，则共存。


然后编译将会自动生成以下代码：

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

### BO 转换为 VO

如下新建 `VOGenerator`：

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

还是通过 `@OOOs` 注解来指定要生成的类，但这里使用了 `ooosPackages` 来指定哪些包下面的类需要进行转换。

转换源为上面生成的：`User_BO` 和 `PetBO`，生成的类名为 `UserVO` 和 `PetVO`。

在 `UserVO` 中扩展了两个字段：

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

从转换源的 `gender` 字段扩展出 `genderDesc` （用于在 View 上进行展示），类型为 `String` ，并且 `replace = false`（`gender` 与 `genderDesc` 共存）：

- **conversionMethodName：**指定转换方法，从 `gender` 转换为 `genderDesc`。默认为不设置。
- **inverseConversionMethodName：**指定逆转换方法，从 `genderDesc` 转换为 `gender`。默认为不设置。

> **注意：**`conversionMethodName` 和 `inverseConversionMethodName` 方法指定方法名字时，方法签名必须满足以下其一：
> - `public static [转换目标类型] conversionXxx([转换源字段类型] param)`
> -  `public static [转换目标类型] conversionXxx([转换源 class 类型] param1, [转换源字段类型] param2)`
> 如上面 `gender` 和 `genderDesc` 的转换：
> - `public static String conversionGender(UserVO userVO, Integer gender)`
> - `public static Integer inverseConversionGender(String genderDesc)`

通过设置以上两个方法，`gender` 和 `genderDesc` 两个字段会实现互相绑定，改变其中一个字段，另一个字段也会自动发生改变。

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

`UserVO` 中还从转换源的 `age` 扩展了一个 `ageDesc` 属性（替换掉 `age` 字段，不共存），并指定了 `conversionMethodName`，但是转换方法并不在 `VOGenerator` 类中，而是在 `AgeConversion` 类中，所以需要显示地进行指定 `conversionMethodClass`。

- **conversionMethodClass：**转换方法所在的 Class，默认不设置则表示在当前的 `Generator` 类中。

另外 `PetVO` 扩展了一个 `ownerUser`。

最后编译生成的代码如下：

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
    // Loss field: age, recommend to use `inverseConversionMethodName`.
    user_BO.setGender(gender);
    return user_BO;
  }
}
```

> **注意：**以上 `User_BO`，由于 `age` 属性是 `replace`，并且只设置了 `conversionMethodName`，并没有设置 `inverseConversionMethodName`，所以在 `toUser_BO()` 方法进行逆转换时会丢失 `age` 属性，所以推荐使用 `inverseConversionMethodName`。

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


