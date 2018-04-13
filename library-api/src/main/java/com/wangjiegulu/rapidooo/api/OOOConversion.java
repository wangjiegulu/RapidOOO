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
public @interface OOOConversion {
    String fieldName();

    String targetFieldName();

    Class<?> targetType();

    Class<?> conversionMethodClass() default Object.class;
    String conversionMethodName();
    String inverseConversionMethodName() default OOOConstants.NOT_SET;

    boolean replace() default false;

}
