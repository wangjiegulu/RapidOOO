package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class ControlDelegatePartBrew implements PartBrew {
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        for (Map.Entry<String, OOOConversionEntry> ee : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = ee.getValue();
            if (!conversionEntry.isControlDelegateSet()) {
                continue;
            }
            FieldSpec controlDelegateField = FieldSpec.builder(conversionEntry.getControlDelegateTypeName(), conversionEntry.getControlDelegateFieldName(),
                    Modifier.PRIVATE, Modifier.FINAL)
                    .initializer("new $T()", conversionEntry.getControlDelegateTypeName())
                    .build();

            result.addField(controlDelegateField);
        }
    }
}
