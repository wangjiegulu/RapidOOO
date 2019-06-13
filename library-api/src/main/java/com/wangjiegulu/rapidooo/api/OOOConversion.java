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
    String targetFieldName();
    Class<?> targetFieldType() default Object.class;
    
    String targetFieldTypeId() default OOOConstants.NOT_SET;

    Class<?> bindMethodClass() default Object.class;
    String bindMethodName() default OOOConstants.NOT_SET;
    String inverseBindMethodName() default OOOConstants.NOT_SET;

    Class<?> conversionMethodClass() default Object.class;
    String conversionMethodName() default OOOConstants.NOT_SET;
    String inverseConversionMethodName() default OOOConstants.NOT_SET;

}
