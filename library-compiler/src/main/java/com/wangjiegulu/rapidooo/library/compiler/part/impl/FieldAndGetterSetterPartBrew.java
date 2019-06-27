package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.api.OOOFieldMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.util.FieldModeMethodStatementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class FieldAndGetterSetterPartBrew implements PartBrew {
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        buildAllContinuingFields(oooEntry, result);
        buildConversionsFields(oooEntry, result);
    }

    private void buildAllContinuingFields(OOOEntry oooEntry, TypeSpec.Builder result) {
        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()) {
            OOOFieldEntry xFieldEntry = fieldE.getValue();

            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(xFieldEntry.getTypeName(), xFieldEntry.getSimpleName(), xFieldEntry.getModifiers())
                    .addJavadoc("Field name: {@link $T#$L}\n",
                            oooEntry.getFromTypeName(),
                            xFieldEntry.getSimpleName()
                    );
            result.addField(fieldSpecBuilder.build());

            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(xFieldEntry.getSimpleName(), xFieldEntry.getTypeName());
            // add getter method
            result.addMethod(PoetUtil.obtainGetterMethodsBuilder(xFieldEntry.getSimpleName(), xFieldEntry.getTypeName(), getterSetterMethodNames).build());
            // add setter method
            MethodSpec.Builder setterMethodBuilder = PoetUtil.obtainSetterMethodsBuilderDefault(xFieldEntry.getSimpleName(), xFieldEntry.getTypeName(), getterSetterMethodNames);

            // 数据 setter 绑定关联
            for (HashMap.Entry<String, OOOConversionEntry> compareCE : oooEntry.getConversions().entrySet()) {
                OOOConversionEntry compareConversionEntry = compareCE.getValue();
                // 只有 bind mode 才需要关联
                if (OOOFieldMode.BIND == compareConversionEntry.getFieldMode()) {
                    for (Map.Entry<String, IOOOVariable> bindMethodParamE : compareConversionEntry.getBindTargetParamFields().entrySet()) {
                        IOOOVariable bindMethodParam = bindMethodParamE.getValue();
                        // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                        if (TextUtil.equals(xFieldEntry.getSimpleName(), bindMethodParam.fieldName())) {
                            FieldModeMethodStatementUtil.buildBindStatement(oooEntry, compareConversionEntry, setterMethodBuilder, this.getClass().getSimpleName());
                        }
                    }
                }
            }
            result.addMethod(setterMethodBuilder.build());

            // Default extra getter method for Boolean field
            if (ElementUtil.isSameType(xFieldEntry.getTypeName(), Boolean.class)) {
                result.addMethod(PoetUtil.obtainExtraBooleanGetterMethodsBuilder(xFieldEntry.getSimpleName(), getterSetterMethodNames).build());
            }

        }
    }

    private void buildConversionsFields(OOOEntry oooEntry, TypeSpec.Builder result) {
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry xConversionEntry = conversionFieldE.getValue();
            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(xConversionEntry.getTargetFieldType(), xConversionEntry.getTargetFieldName(), Modifier.PRIVATE)
                    .addJavadoc("Field name conversion : {@link $T}\n",
                            oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType()
                    );
            result.addField(fieldSpecBuilder.build());

            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(xConversionEntry.getTargetFieldName(), xConversionEntry.getTargetFieldType());
            // add getter method
            result.addMethod(PoetUtil.obtainGetterMethodsBuilder(xConversionEntry.getTargetFieldName(), xConversionEntry.getTargetFieldType(), getterSetterMethodNames).build());
            // add setter method
            MethodSpec.Builder setterMethodBuilder = PoetUtil.obtainSetterMethodsBuilderDefault(xConversionEntry.getTargetFieldName(), xConversionEntry.getTargetFieldType(), getterSetterMethodNames);

            // 数据 setter 绑定关联
            for (HashMap.Entry<String, OOOConversionEntry> compareCE : oooEntry.getConversions().entrySet()) {
                OOOConversionEntry compareConversionEntry = compareCE.getValue();

                // 只有 bind mode 才需要关联
                if (OOOFieldMode.BIND == compareConversionEntry.getFieldMode()) {
                    for (Map.Entry<String, IOOOVariable> bindMethodParamE : compareConversionEntry.getInverseBindTargetParamFields().entrySet()) {
                        IOOOVariable bindMethodParam = bindMethodParamE.getValue();
                        // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                        if (TextUtil.equals(xConversionEntry.getTargetFieldName(), bindMethodParam.fieldName())) {
                            FieldModeMethodStatementUtil.buildInverseBindStatement(oooEntry, compareConversionEntry, setterMethodBuilder, this.getClass().getSimpleName());
                        }
                    }

                    for (Map.Entry<String, IOOOVariable> bindMethodParamE : compareConversionEntry.getBindTargetParamFields().entrySet()) {
                        IOOOVariable bindMethodParam = bindMethodParamE.getValue();
                        // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                        if (TextUtil.equals(xConversionEntry.getTargetFieldName(), bindMethodParam.fieldName())) {
                            FieldModeMethodStatementUtil.buildBindStatement(oooEntry, compareConversionEntry, setterMethodBuilder, this.getClass().getSimpleName());
                        }
                    }
                }

            }


            result.addMethod(setterMethodBuilder.build());

            // Default extra getter method for Boolean field
            if (ElementUtil.isSameType(xConversionEntry.getTargetFieldType(), Boolean.class)) {
                result.addMethod(PoetUtil.obtainExtraBooleanGetterMethodsBuilder(xConversionEntry.getTargetFieldName(), getterSetterMethodNames).build());
            }
        }
    }

}
