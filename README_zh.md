# RapidOOO

## 什么是 RapidOOO

我们在领域驱动设计中经常会在不同层级之间传递数据，例如 `VO`, `PO`, `DO`, `DTO`, `BO`等。Android 的开发中也经常会遇到这些情况，比如在 [Android-CleanArchitecture](https://github.com/android10/Android-CleanArchitecture) 的 [UserModelDataMapper::transform](https://github.com/android10/Android-CleanArchitecture/blob/master/presentation/src/main/java/com/fernandocejas/android10/sample/presentation/mapper/UserModelDataMapper.java#L42), [UserEntityDataMapper::transform](https://github.com/android10/Android-CleanArchitecture/blob/master/data/src/main/java/com/fernandocejas/android10/sample/data/entity/mapper/UserEntityDataMapper.java#L42) 等。手工地进行拷贝转换的过程不但繁琐，而且错误的风险比较大，在新增、删除字段时也增加了维护的成本。[Dozer](http://dozer.sourceforge.net/documentation/about.html) 可以很好地解决这个问题，但是在 Android 上可能就不太适用了。

`RapidOOO` 根据 POJO 类编译时灵活地自动生成支持扩展互相绑定的领域对象。

## 怎么使用

在 `build.gradle` 中配置依赖 `RapidOOO`：

```groovy
implementation "com.github.wangjiegulu:rapidooo-api:x.x.x"
annotationProcessor "com.github.wangjiegulu:rapidooo-compiler:x.x.x"
```

注意：

- `x.x.x` 为版本号，[最新版本点这里检查](https://search.maven.org/search?q=rapidooo)。

### 基本使用

存在 `Person` 类如下：

```java
public class Person {
    private String name;
    private String country;
    // getter / setter
}
```

要自动生成 `PersonBO` 类，则创建 `Generator` 类并进行如下配置：

```java
@OOOs(suffix = "BO",
        ooos = {
            @OOO(from = Person.class),
            ...
        }
)
public class DemoBOGenerator {}
```

注意：

- `Generator` 类的类名可以任意。

构建之后，将会生成如下 `PersonBO` 与 `Person` 一样的类：

```java
public class PersonBO {
  private String name;
  private String country;

  public PersonBO() {
  }

  public void fromPerson(Person person) {
    this.name = person.getName();
    this.country = person.getCountry();
  }

  public static PersonBO create(Person person) {
    PersonBO personBO = new PersonBO();
    personBO.fromPerson(person);
    return personBO;
  }

  public void toPerson(Person person) {
    person.setName(name);
    person.setCountry(country);
  }

  public Person toPerson() {
    Person person = new Person();
    toPerson(person);
    return person;
  }
}
```

### 注解配置自动生成

> 假设：通过 `UserBO` 类自动生成 `UserVO` 类，则：
>
> - `UserBO` 称为：**源类**
> - `UserVO` 称为：**目标类**

首先要创建 `Generator` 类，然后在该类上增加注解配置，该类表示 **一次** 自动生成转换，**一次** 自动生成转换中可以生成 **多个** 领域类。

重要的注解配置如下：

#### `@OOOs` 注解

> `@OOOs` 用于指定该次自动生成转换的所有配置。

- **fromSuffix**：`目标类` 后缀，比如从 `UserBO` 自动生成 `UserVO`，则 `fromSuffix` 为 `UserBO` 的后缀：`BO`。如果 `源类` 没有后缀，则不填。
- **suffix**：`目标类` 后缀，比如从 `UserBO` 自动生成 `UserVO`，则 `suffix` 为 `UserVO` 的后缀：`VO`。
- **ooosPackages**：`源类` 所在的包数组，`RapidOOO` 将会把这些包中的类都会进行自动生成。
- **ooos**：`@OOO` 注解数组，`目标类` 的自定义配置（后面会讲到）。

#### `@OOO` 注解

> `@OOOs` 用于指定 `目标类` 的自定义配置。

- **id**：该 `目标类` 的id，id 必须以 `#` 号开头。
- **fromSuffix**：同 `@OOOs` 中的 `fromSuffix`，此优先级高。
- **suffix**：同 `@OOOs` 中的 `suffix`，此优先级高。
- **from**：指定 `源类` 的 `Class`。
- **includes**：指定该 `目标类` 包含哪些字段（Field），不能与 `excludes` 同时使用。
- **excludes**：指定该 `目标类` 不包含哪些字段（Field），不能与 `includes` 同时使用。
- **targetSupperType**：指定该 `目标类` 的父类的 Class。
- **targetSupperTypeId**：指定该 `目标类` 的父类的 id 表达式，id 必须以 `#` 号开头。
- **pool**：针对该 `目标类` 类的对象池相关配置（后面会讲到）。
- **parcelable**：配置该 `目标类` 类是否实现 `Parcelable` 接口（前提是 `源类` 是实现了 `Parcelable` 接口）。
- **conversions**：`@OOOConversion` 数组，表示该 `目标类` 中需要进行特殊转换的字段配置（后面会讲到）。

#### `OOOConversion` 注解

> `@OOOs` 用于指定 `目标类` 中某个字段的自定义配置。

- **targetFieldName**：该字段的名字。
- **targetFieldType**：指定该字段的 Class。
- **targetFieldTypeId**：指定该字段的 id 表达式。
- **attachFieldName**：表示字段模式（后面会讲到）为 `attach`，指定与 `源类` 的哪个字段 attach。
- **bindMethodClass**：表示字段模式（后面会讲到）为 `bind`，指定绑定转换的方法所在的类（后面会讲到）。
- **bindMethodName**：表示字段模式（后面会讲到）为 `bind`，指定绑定转换的方法（后面会讲到）。
- **inverseBindMethodName**：表示字段模式（后面会讲到）为 `bind`，指定绑定逆转换的方法（后面会讲到）。
- **conversionMethodClass**：表示字段模式（后面会讲到）为 `conversion`，指定转换的方法所在的类（后面会讲到）。
- **conversionMethodName**：表示字段模式（后面会讲到）为 `conversion`，指定转换的方法（后面会讲到）。
- **inverseConversionMethodName**：表示字段模式（后面会讲到）为 `conversion`，指定逆转换的方法（后面会讲到）。
- **parcelable**：是否可以实现 `parcelable` 接口。
- **controlDelegate**：配置该字段的控制委托（后面会讲到）。

### 转换方法自动生成

通过注解配置，在构建后会自动生成如下两个方法：

- **create() static 方法**：`源类` 转为 `目标类`，如：`UserVO userVO = UserVO.create(userBO)`
- **toXxx() 成员方法**：`目标类` 转为 `源类`，如：`userVO.toUserBO()`。

### id 类型表达式

> 通过字符串来表示类型（包括 `id`）。

- ≈ 的 `id` 必须以 `#` 号开头。
- 在 `targetFieldTypeId` 等字段中可以使用 id 表达式来表达复杂类型（后面会提到）。

### 链式转换

通过配置支持链式的自动生成：从 `Person` 生成 `PersonBO`，从 `PersonBO` 生成 `PersonVO`...

```java
// 生成 PersonBO
@OOOs(suffix = "BO", ooos = {@OOO(from = Person.class)})
public class DemoBOGenerator {}

// 生成 PersonVO
@OOOs(suffix = "VO", fromSuffix = "BO", ooos = {@OOO(from = PersonBO.class)})
public class DemoVOGenerator {}
```

### 继承

如果 `Abbey` 类继承了 `Person` 类，如下：

```java
public class Abbey extends Person {
    // ...
}
```

可以通过以下配置来生成 `AbbeyBO` 类，并且继承同样是生成的 `PersonBO`：

```java
@OOOs(suffix = "BO",
        ooos = {
                @OOO(id = "#id_bo_person", from = Person.class),
                @OOO(from = Abbey.class, targetSupperTypeId = "#id_bo_person")
        }
)
public class ExtBOGenerator {
}
```

- 先配置 `Person` 类，然后设置它的 `id`（任意字符，但是必须是 `#` 开头），如 `#id_bo_person`（此配置会生成 `PersonBO` 类）
- 然后配置 `Abbey` 类，设置 `targetSupperTypeId` 为 `#id_bo_person`（为 `Abbey` 类设置父类）

最终生成如下：

```java
public class AbbeyBO extends PersonBO {
    // ...
}
```

### 支持的接口

默认情况下，`源类` 实现的接口不会在生成的 `目标类` 中自动也去实现。

但是以下两个接口**除外**：

#### Serializable

如果 `源类` 实现了 `Serializable` 接口，在生成的 `目标类` 中也会自动默认实现该接口。如下：

```java
// 源类
public class User implements Serializable{
    // ...
}

// 配置 generator
@OOOs(suffix = "BO", ooos = {@OOO(from = User.class)})
public class BOGenerator {}

// 目标类（RapidOOO 自动生成）
public class UserBO implements Serializable{
    // ...
}
```

#### Parcelable

如果 `源类` 实现了 `Parcelable` 接口，在生成的 `目标类` 中也会自动默认实现该接口（包括自动生成 `构造方法`、`CREATOR`、`writeToParcel()`、`describeContents` 等）。如下：

```java
// 源类
public class Message implements Parcelable {
    public static final Creator<Message> CREATOR = new Creator<Message>() { ... }
    protected Message(Parcel in) { ... }
    @Override
    public int describeContents() { ... }
    @Override
    public void writeToParcel(Parcel dest, int flags){ ... }
}

// 配置 generator
@OOOs(suffix = "BO", ooos = {@OOO(from = Message.class)})
public class BOGenerator {}

// 目标类（RapidOOO 自动生成）
public class MessageBO implements Parcelable {
    public static final Creator<MessageBO> CREATOR = new Creator<MessageBO>() { ... }
    protected MessageBO(Parcel in) { ... }
    @Override
    public int describeContents() { ... }
    @Override
    public void writeToParcel(Parcel dest, int flags){ ... }
}
```

但是你可以通过 `@OOO` 注解中的 `parcelable` 设置为 `false` 来禁用所有字段不进行序列化。

如果你只需要生成类中的某个字段不进行 parcelable 序列化，可以通过 `@OOOConversion` 注解中的 `parcelable` 设置为 `false` 来禁用它。

### 包含排除字段

可以通过 `@OOO` 中的 `include` 和 `excludes` 两个属性来指定 `源类` 中的哪些字段 **需要/不需要** 生成在 `目标类` 中。如下：

```java
@OOOs(suffix = "BO", ooos = {
        @OOO(from = Message.class, excludes = {"chat", "text"})
    })
public class BOGenerator {}
```

以上，在生成的 `MessageBO` 中不会包含来自 `Message` 的 `chat` 和 `text` 两个字段。

`includes` 的使用方式也是一样。

**注意：**
> - `includes` 和 `excludes` 如果都没有设置，则默认为 `源类` 中的所有字段 **都会** 生成在 `目标类` 中。
> - `includes` 和 `excludes` 不能同时使用，否则编译会报错。

### 字段模式 Field Mode

可以在 `@OOO` 注解中通过 `@OOOConversion` 注解来对每个 `目标类` 中的每个字段通过不同的 `字段模式（Field Mode）` 来进行配置。

具体的配置方式如下：

#### 字段增加

在 `源类` 生成 `目标类` 时，可以在 `目标类` 中增加一些 `源类` 中不存在的字段，如下：

```java
// `源类`
public class Message {
    private Integer id;
    private String content;
    // getter / setter
}

// 配置 generator
@OOOs(suffix = "BO",
    ooos = {
        @OOO(from = Message.class,
            conversions = {
                    @OOOConversion(
                            targetFieldName = "read",
                            targetFieldType = boolean.class
                    )
            }
    }
)
public class DemoBOGenerator {}

// `目标类`
public class Message {
    private Integer id;
    private String content;
    private boolean read;
    // getter / setter
}
```

以上通过 `@OOOConversion` 注解增加了一个字段名为 `read`、类型为 `boolean` 的字段，并生成了 `getter/setter` 方法。

> **注意**：这个 `read` 字段与 `Message` 类中的任何字段都没有任何关系。

#### 字段连接 Attach Mode

如果我需要在 `目标类` 增加一个字段，并且把这个字段与 `源类` 中的相似类型的字段 **连接** 起来。

或者说用新的字段替换掉 `源类` 的相似类型的字段，比如：

```java
// `源类`
public class Message {
    private Integer id;
    private String content;
    // getter / setter
}

// 配置 generator
@OOOs(suffix = "BO",
    ooos = {
        @OOO(from = Message.class,
            // 排除掉源类中的 content 字段
            excludes = {"content"},
            conversions = {
                    @OOOConversion(
                            targetFieldName = "text",
                            targetFieldType = String.class,
                            // 使用 attach 模式把 `text` 字段关联到 `content` 字段
                            attachFieldName = "content"
                    )
            }
    }
)
public class DemoBOGenerator {}

// `目标类`
public class Message {
    private Integer id;
    // 源类中的 content 被替换成了目标中的 text 字段
    private String text;
    // getter / setter
}
```

如上注释：首先通过 `excludes` 排除掉要被关联的 `content` 字段，然后再用 `@OOOConversion` 注解配置新的 `text` 字段，并通过 `attachFieldName` 连接到 `源类` 的 `content` 字段。

最终的结果是，`目标类` 中的 `content` 被替换成了 `text`。

**那怎么体现了 `连接` 这个特点呢？**

如果是 **连接（attach）** 模式，则 `目标类` 中还会在某些方法中生成以下代码来确保新增字段与原来的字段是处于**连接状态**的：

```java
public class MessageBO {
    public static MessageBO create(Message message) {
        MessageBO messageBO = new MessageBO();
        messageBO.fromMessage(message);
        return messageBO;
    }
    public void fromMessage(Message message) {
        // ...
        // 在 Message 类型转 MessageBO 类型时，这里 MessageBO 的 text 被赋值为 Message 的 content
        this.text = message.getContent();
        // ...
    }

    public Message toMessage() {
        Message message = new Message();
        toMessage(message);
        return message;
    }
    public void toMessage(Message message) {
        // ...
        // 在 MessageBO 类型转 Message 类型时，这里 Message 的 content 被赋值为 MessageBO 的 text
        message.setContent(this.text);
        // ...
    }
}
```

由此，`Message` 中的 `content` 与 `MessageBO` 中的 `text` 字段达成了连接，于是如下单元测试通过：

```java
final String CONTENT = "message content";
// 构建一个 Message 对象
Message message = new Message();
message.setContent(CONTENT);

// 从 Message 对象转换成一个新的 MessageBO
MessageBO messageBO = MessageBO.create(message);
Assert.assertEquals(CONTENT, messageBO.getText());

// 通过 MessageBO 对象转换成一个新的 Message 对象
Message newMessage = messageBO.toMessage();
Assert.assertEquals(CONTENT, newMessage.getContent());
```

以上的例子是 `源类` `Message` 的 `content` 与 `目标类` `MessageBO` 的 `text` 之间的连接，两者都是同一种类型： `String`。

除了支持**同一种**类型的连接之外，其实还支持以下的类型的连接：

|序号|源类字段|目标类字段|举例（源 -> 目标）|
|---|---|---|---|
|1)|某种任意类型|某种相同的任意类型|`FooBar` <br/> -> <br/> `FooBar`|
|2)|某种 `Map` 类型|另一种相同或不同的 `Map` 类型（泛型相同）|`HashMap` <br/> -> <br/> `LinkedHashMap`|
|3)|某种 `List` 类型|另一种相同或不同的 `List` 类型（泛型相同）|`ArrayList` <br/> -> <br/> `LinkedList`|
|4)|某种数组类型|数组类型|`Foo[]` <br/> -> <br/> `Foo[]`|
|5)|某种其它源类型|某种其它目标类型|`Chat` <br/> -> <br/> `ChatBO`|
|6)|序号`2)``3)``4)`与`5)`的组合|序号`2)``3)``4)`与`5)`的组合|`Map<String, Chat>` <br/> -> <br/> `HashMap<String, ChatBO>` <br/> 或 <br/> `ArrayList<Chat>` <br/> -> <br/> `List<ChatBO>`|


#### 字段转换 / 逆转换 Conversion Mode

使用 `attach` 这种 **产生连接** 以**自动**在 `源` 和 `目标` 字段中转换的方式，在有些实际场景可能无法满足需求，所以 `RapidOOO` 还提供了更为灵活的 `字段转换` 模式，你可以通过 `@OOOConversion` 注解的 `conversionMethodName` 和 `inverseConversionMethodName` 来指定转换 / 逆转换的方法，如下例子：

```java
// 源类
public class User{
    private String username;
    // 1 / 0 / -1
    private Integer gender;
}

// 配置 generator
@OOOs(suffix = "VO",
    ooos = {
        @OOO(from = User.class,
            // 排除掉源类中的 gender 字段
            excludes = {"gender"},
            conversions = {
                    @OOOConversion(
                            targetFieldName = "genderDesc",
                            targetFieldType = String.class,
                            // 使用 conversion 模式通过 `conversionGenderDesc` 把 `gender` 字段转换为 `genderDesc` 字段
                            conversionMethodName = "conversionGenderDesc",
                            // 使用 conversion 模式通过 `inverseConversionGender` 把 `genderDesc` 字段转换为 `gender` 字段
                            inverseConversionMethodName = "inverseConversionGender"
                    )
            }
    }
)
public class VOGenerator {
    public static String conversionGenderDesc(Integer gender, String username) {
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

    public static void inverseConversionGender(String genderDesc, UserVO self, User_BO other) {
        int gender;
        switch (genderDesc) {
            case "male":
                gender = 1;
                break;
            case "female":
                gender = 0;
                break;
            default:
                gender = -1;
                break;
        }
        other.setGender(gender);
    }
}

// 目标类（RapidOOO 自动生成）
public class UserVO{
    private String username;
    // male / female / unknown
    private String genderDesc;

    // ...
    public void fromUser(User user) {
        this.genderDesc = VOGenerator.conversionGenderDesc(user_BO.getGender(), this.username);
    }
    // ...

    public void toUser(User user) {
        VOGenerator.inverseConversionGender(this.genderDesc, this, user);
    }
}
```

先看 `gender` 转换为 `genderDesc` 的配置：

通过指定 `@OOOConversion` 注解中的 `conversionMethodName` 方法，此方法可以编写在本次转换的 `Generator` 类中，也就是上面的 `VOGenerator`，方法名为 `conversionGenderDesc`，并且该方法必须满足以下几个条件：

- 方法必须是 `public static`
- 方法的返回值必须为 `targetFieldType` 指定的类型，方法返回值即为转换结果。
- 参数表示你本次转换需要使用到的对象，可以**任意**多个，但是**必须**满足以下几个条件**之一**：
    - 参数名为对应的 `目标类` 中的字段名，并且该参数类型与对应的字段要**一致**，生成的代码在调用时会把对应字段转入到该方法。
    - 参数名为 `self`，类型为 `目标类`，生成的代码在调用时会把当前的 `目标类` 对象传入到该方法。
    - 参数名为 `other`，类型为 `源类`，生成的代码在调用时会把当前的 `源类` 对象传入到该方法。

`genderDesc` 转换为 `gender` 的配置：

通过指定 `@OOOConversion` 注解中的 `inverseConversionMethodName` 方法，此方法可以编写在本次转换的 `Generator` 类中，也就是上面的 `VOGenerator`，方法名为 `inverseConversionGender`，并且该方法必须满足以下几个条件：

- 方法必须是 `public static`
- 方法的返回值必须为 `void`
- 参数表示你本次转换需要使用到的对象，可以**任意**多个，但是**必须**满足以下几个条件**之一**：
    - 参数名为对应的 `目标类` 中的字段名，并且该参数类型与对应的字段要**一致**，生成的代码在调用时会把对应字段转入到该方法。
    - 参数名为 `self`，类型为 `目标类`，生成的代码在调用时会把当前的 `目标类` 对象传入到该方法。
    - 参数名为 `other`，类型为 `源类`，生成的代码在调用时会把当前的 `源类` 对象传入到该方法。

转换结果通过调用 `other` 方法参数去进行设置（setter）。

> **注意**：
>
> - `conversionMethodName` 和 `inverseConversionMethodName` 也可以只设置其中一个。
>
> - 一般情况下使用 **字段转换** 模式的同时应该把对应的所有相关的字段（`conversionMethodName` 和 `inverseConversionMethodName` 中参数对应的所有字段）会 `exclude` 掉。
>
> - `conversionMethodName` 和 `inverseConversionMethodName` 的对应的方法默认是在 `Generator` 中编写，但是你也可以通过指定 `@OOOConversion` 注解的 `conversionMethodClass` 来指定转换方法所在的类。


#### 字段绑定 / 逆绑定 Bind Mode

如果需要把转换相应的 `源类` 字段继续保留，则可以使用 `字段绑定（Bind）` 模式。使用方式与 `字段转换（Conversion Mode）` 模式比较类似，同样提供了两个方法 `bindMethodName` 和 `inverseBindMethodName` 来指定绑定 / 逆绑定的方法，如下例子：

```java
// 源类
public class User{
    private Integer age;
}

// 配置 generator
@OOOs(suffix = "VO",
    ooos = {
        @OOO(from = User.class,
            // 不排除掉源类中的 age 字段
            conversions = {
                    @OOOConversion(
                            targetFieldName = "ageDesc",
                            targetFieldType = String.class,
                            // 指定绑定方法所在的类 AgeBinder
                            bindMethodClass = AgeBinder.class,
                            // 使用 bind 模式通过 `bindAgeDesc` 把 `age` 字段转换为 `ageDesc` 字段
                            bindMethodName = "bindAgeDesc",
                            // 使用 bind 模式通过 `inverseBindAgeDesc` 把 `ageDesc` 字段转换为 `age` 字段
                            inverseBindMethodName = "inverseBindAgeDesc"
                    )
            }
    }
)
public class VOGenerator {
}

// AgeBinder.java
public class AgeBinder {
    public static String bindAgeDesc(Integer age) {
        if (null == age || age < 0) {
            return "unknown";
        }
        return age + " years old";
    }
    public static void inverseBindAgeDesc(String ageDesc, UserVO self) {
        Integer age;
        if (null == ageDesc) {
            age = -1;
        }else{
            age = Integer.valueOf(ageDesc.split(" ")[0]);
        }
        self.setAge(age);
    }
}

// 目标类（RapidOOO 自动生成）
public class UserVO{
    private Integer age;
    private String ageDesc;

    public void fromUser(User user) {
        this.age = user_BO.getAge();
        // User 转换为 UserVO 时，通过 bindAge 方法来初始化 ageDesc
        this.ageDesc = AgeBinder.bindAge(this.age);
    }

    public void setAge(Integer age) {
        this.age = age;
        // 当 age 改变时，通过 bind 方法自动实时更新 ageDesc 的值
        this.ageDesc = AgeBinder.bindAge(this.age);
    }

    public void setAgeDesc(String ageDesc) {
        this.ageDesc = ageDesc;
        // 当 ageDesc 改变时，通过 inverseBind 方法自动实时更新 age 的值
        AgeBinder.inverseBindAge(this.ageDesc, this);
    }
}
```

如上，通过设置 `bindMethodName` 和 `inverseBindMethodName` 来设置绑定和逆绑定的方法，并设置 `bindMethodClass` 来指定这些方法所在的类。

生成的 `目标类` 中，与 `字段转换 Conversion Mode` 不同的是，当某个字段发生改变时（setter 方法被调用），对应绑定的字段也会被更新（绑定和逆绑定方法被调）。也就是说，`字段绑定 Bind Mode` 时，字段和该字段对应绑定的字段将会实时双向同步数据。

`age` 转换为 `ageDesc` 的配置：

通过指定 `@OOOConversion` 注解中的 `bindMethodName` 方法，该方法必须满足以下几个条件：

- 方法必须是 `public static`
- 方法的返回值必须为 `targetFieldType` 指定的类型，方法返回值即为转换结果。
- 参数表示你本次转换需要使用到的对象，可以**任意**多个，但是**必须**满足以下几个条件**之一**：
    - 参数名为对应的 `目标类` 中的字段名，并且该参数类型与对应的字段要**一致**，生成的代码在调用时会把对应字段转入到该方法。
    - 参数名为 `self`，类型为 `目标类`，生成的代码在调用时会把当前的 `目标类` 对象传入到该方法。
    - 参数名为 `other`，类型为 `源类`，生成的代码在调用时会把当前的 `源类` 对象传入到该方法。

`ageDesc` 转换为 `age` 的配置：

通过指定 `@OOOConversion` 注解中的 `inverseBindMethodName` 方法，该方法必须满足以下几个条件：

- 方法必须是 `public static`
- 方法的返回值必须为 `void`
- 参数表示你本次转换需要使用到的对象，可以**任意**多个，但是**必须**满足以下几个条件**之一**：
    - 参数名为对应的 `目标类` 中的字段名，并且该参数类型与对应的字段要**一致**，生成的代码在调用时会把对应字段转入到该方法。
    - 参数名为 `self`，类型为 `目标类`，生成的代码在调用时会把当前的 `目标类` 对象传入到该方法。
    - 参数名为 `other`，类型为 `源类`，生成的代码在调用时会把当前的 `源类` 对象传入到该方法。

转换结果通过调用 `other` 方法参数去进行设置（setter）。

> **注意**：
>
> - `bindMethodName` 和 `inverseBindMethodName` 也可以只设置其中一个。
>
> - 一般情况下使用 **字段绑定** 模式的同时应该保留所有相关的字段（`bindMethodName` 和 `inverseBindMethodName` 中参数对应的所有字段）。
>
> - `bindMethodName` 和 `inverseBindMethodName` 的对应的方法默认是在 `Generator` 中编写，但是你也可以通过指定 `@OOOConversion` 注解的 `bindMethodClass` 来指定转换方法所在的类。


### 对象池

为了解决在不同领域模型中对象频繁地互相转换所带来的性能问题，RapidOOO 也支持使用对象池来构建领域对象，方法如下：

```java
// 源类
@OOOs(suffix = "BO", ooos = {
        @OOO(
            from = Pet.class,
            // ...
            pool = @OOOPool(acquireMethod = "acquirePetBO", releaseMethod = "releasePetBO")
        )
})

// 配置 generator
public class BOGenerator {
    private static Pools.Pool<PetBO> petBOPool = new Pools.SimplePool<>(3);

    public static PetBO acquirePetBO() {
        PetBO petBO = petBOPool.acquire();
        return null == petBO ? new PetBO() : petBO;
    }

    public static void releasePetBO(PetBO petBO) {
        petBOPool.release(petBO);
    }
}

// 目标类（RapidOOO 自动生成）
public class PetBO{
    // ...
    public static PetBO create(Pet pet) {
        PetBO petBO = BOGenerator.acquirePetBO();
        petBO.fromPet(pet);
        return petBO;
    }
    // ...
    public void release() {
        BOGenerator.releasePetBO(this);
    }
    // ...
}

// 使用方法
Pet pet = new Pet();
PetBO petBO = PetBO.create(pet);
petBO.release();
```

如上，通过 `@OOO` 注解中的 `pool` 来配置对象池的 `acquireMethod` 和 `releaseMethod` 方法。

其中 `acquireMethod` 方法必须满足如下的要求：

- 方法**必须**是 `public static`
- 方法**无参数**
- 方法返回值为 `目标类` 类型

其中 `releaseMethod` 方法必须满足如下的要求：

- 方法**必须**是 `public static`
- 方法参数**必须**是只有 1 个，并且类型为 `目标类` 类型
- 方法返回值为 `void`

### 复杂类型

使用 `@OOOConversion` 扩展字段时，目前支持以下多种类型：

#### Array

使用数组时，需要按照如下设置 `targetFieldTypeId`：

```java
// ...
@OOOConversion(
        targetFieldName = "chatVOs",
        targetFieldTypeId = "#id__ChatVO[]"
        // ...
),
@OOOConversion(
        targetFieldName = "comments",
        targetFieldTypeId = "java.lang.String[]"
        // ...
),
// ...
```

如上：支持 `id` 和具体类型的权限定名，后面加上 `[]`，表示数组。

#### List

使用 `List` 时，需要按照如下设置 `targetFieldTypeId`：

```java
// ...
@OOOConversion(
        targetFieldName = "chatVOs",
        targetFieldTypeId = "java.util.List<#id__ChatVO>"
        // ...
),
@OOOConversion(
        targetFieldName = "comments",
        targetFieldTypeId = "java.util.ArrayList<java.lang.String>"
        // ...
),
// ...
```

如上：泛型支持 `id` 和具体类型的权限定名，`List` 支持权限定名的 `List`, `ArrayList`, `LinkedList` 等等。

#### Map

使用 `Map` 时，需要按照如下设置 `targetFieldTypeId`：

```java
// ...
@OOOConversion(
        targetFieldName = "chatVOs",
        targetFieldTypeId = "java.util.Map<java.lang.String, #id__ChatBO>"
        // ...
),
@OOOConversion(
        targetFieldName = "comments",
        targetFieldTypeId = "java.util.Map<java.lang.String, java.lang.Integer>"
        // ...
),
// ...
```

如上：泛型（`Key` 和 `Value`）支持 `id` 和具体类型的权限定名，`Map` 支持权限定名的 `Map`, `HashMap`, `TreeMap` 等等。

### 控制委托 Control Delegate

如果你需要对转换的字段做一些额外的处理，你可以通过 `控制委托 Control Delegate` 来实现，比如通过 `OOOLazyControlDelegate` 来实现懒初始化字段。

> **注意**：目前 `控制委托 Control Delegate` **只支持** `bindMethod` 和 `conversionMethod`，并**不支持** `inverseBindMethod` 和 `inverseConversionMethod`

#### OOOLazyControlDelegate

什么情况下可能需要使用到懒加载？

比如通过 `Message` 中的 `videoUrl` 转换为 `MessageVO` 中的 `MediaPlayer` 类，避免资源浪费，`MediaPlayer` 实例应该作为懒加载的对象，如下：

```java
@OOOConversion(
    targetFieldName = "lazyVideoPlayer",
    targetFieldType = MediaPlayer.class,
    // 设置转换方法
    conversionMethodName = "conversionLazyVideo",
    // 设置为懒加载的控制委托
    controlDelegate = OOOLazyControlDelegate.class
),

public static MediaPlayer conversionLazyVideo(MessageVO self, String videoUrl){
    OOOLazy<MediaPlayer> lazyMediaPlayer = self.getLazyVideoPlayer();
    if(lazyMediaPlayer.isInitialized()){
        lazyMediaPlayer.get().stop();
        lazyMediaPlayer.get().release();
    }
    if(null == videoUrl){
        return null;
    }
    MediaPlayer mediaPlayer = new MediaPlayer();
    try {
        mediaPlayer.setDataSource(videoUrl);
        mediaPlayer.prepareAsync();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return mediaPlayer;
}
```

以上，通过 `@OOOConversion` 中的 `controlDelegate` 配置对应的控制委托的 `Class` 类型即可，这里配置了 `OOOLazyControlDelegate.class`。

`conversionLazyVideo` 方法只会在 `OOOLazy<MediaPlayer>` 调用 `getter` 时才会被调用。

#### 自定义 ControlDelegate

`ControlDelegate` 还支持自定义扩展，继承 `OOOControlDelegate` 类，实现 `invoke` 方法即可。

其中 invoke 中的两个参数：

- `Func0R<T> inputFunc`：该方法调用 `call()` 就会直接调用 `bindMethod` 或 `conversionMethod` 方法，返回 T 类型结果
- `Func1<R> outputFunc`：该方法调用后，将会把输入的值赋值给 `目标类` 中的对应字段。

其中 `inputFunc` 和 `outputFunc` 的调用时机可以通过自定义的 `ControlDelegate` 进行控制。

比如以下是扩展的 `OOONewThreadControlDelegate`，表示字段在进行 `bind` / `conversion` 时，将会在新的线程中执行：

```java
public class OOONewThreadControlDelegate<T> implements OOOControlDelegate<T, T> {

    @Override
    public final void invoke(Func0R<T> inputFunc, Func1<R> outputFunc) {
        invokeSafe(new WeakReference<>(inputFunc), new WeakReference<>(outputFunc));
    }

    private void invokeSafe(final WeakReference<Func0R<T>> inputFuncWRef, final WeakReference<Func1<T>> outputFuncWRef) {
        // TODO: 2019-06-26 wangjie
        new Thread(new Runnable() {
            @Override
            public void run() {
                Func0R<T> inputFunc = inputFuncWRef.get();
                Func1<T> outputFunc = outputFuncWRef.get();
                if (null != inputFunc && null != outputFunc) {
                    // 子线程中执行，并把结果赋值
                    outputFunc.call(inputFunc.call());
                }
            }
        }).start();
    }
}
```

> **注意**：以上 `OOONewThreadControlDelegate` 只供自定义实现控制委托方式的参考。

