package com.wangjiegulu.rapidooo.api;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 4/10/18.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface OOO {

    String id() default OOOConstants.NOT_SET;

    String fromSuffix() default OOOConstants.NOT_SET;

    String suffix() default OOOConstants.NOT_SET;

    Class<?> from();

    String[] includes() default {};
    String[] excludes() default {};


    OOOConversion[] conversions() default {};

    Class<?> targetSupperType() default Object.class;
    String targetSupperTypeId() default OOOConstants.NOT_SET;

    OOOPool pool() default @OOOPool();

    boolean parcelable() default true;
}
