package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
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
        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()){
            OOOFieldEntry fieldEntry = fieldE.getValue();

            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(fieldEntry.getTypeName(), fieldEntry.getSimpleName(), fieldEntry.getModifiers())
                    .addJavadoc("Field name: {@link $T#$L}\n",
                            oooEntry.getFromTypeName(),
                            fieldEntry.getSimpleName()
                    );
            result.addField(fieldSpecBuilder.build());

            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(fieldEntry.getSimpleName(), fieldEntry.getTypeName());
            // add getter method
            result.addMethod(PoetUtil.obtainGetterMethodsBuilder(fieldEntry.getSimpleName(), fieldEntry.getTypeName(), getterSetterMethodNames).build());
            // add setter method
            MethodSpec.Builder setterMethodBuilder = PoetUtil.obtainSetterMethodsBuilderDefault(fieldEntry.getSimpleName(), fieldEntry.getTypeName(), getterSetterMethodNames);

            // 数据 setter 绑定关联
            for(HashMap.Entry<String, OOOConversionEntry> ce : oooEntry.getConversions().entrySet()){
                OOOConversionEntry conversionEntry = ce.getValue();
                // 只有 bind mode 才需要关联
                if(OOOControlMode.BIND == conversionEntry.getControlMode()){
                    for(Map.Entry<String, IOOOVariable> variableE : conversionEntry.getBindTargetParamFields().entrySet()){
                        IOOOVariable targetVariable = variableE.getValue();
                        // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                        if(TextUtil.equals(fieldEntry.getSimpleName(), targetVariable.fieldName())){

                            String paramsStr = TextUtil.joinHashMap(conversionEntry.getBindTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                                @Override
                                public String call(IOOOVariable ioooTargetVariable) {
                                    return ioooTargetVariable.inputCode();
                                }
                            });

                            setterMethodBuilder.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc());

                            setterMethodBuilder.addStatement(
                                    "this." + conversionEntry.getTargetFieldName() + " = $T." + conversionEntry.getBindMethodName() + "(" + paramsStr + ")",
                                    conversionEntry.getBindMethodClassType()
                            );
                        }
                    }
                }
            }
            result.addMethod(setterMethodBuilder.build());

            // Default extra getter method for Boolean field
            if(ElementUtil.isSameType(fieldEntry.getTypeName(), Boolean.class)){
                result.addMethod(PoetUtil.obtainExtraBooleanGetterMethodsBuilder(fieldEntry.getSimpleName(), getterSetterMethodNames).build());
            }

        }
    }

    private void buildConversionsFields(OOOEntry oooEntry, TypeSpec.Builder result) {
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()){
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();
            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(conversionEntry.getTargetFieldType(), conversionEntry.getTargetFieldName(), Modifier.PRIVATE)
                    .addJavadoc("Field name conversion : {@link $T}\n",
                            oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType()
                    );
            result.addField(fieldSpecBuilder.build());

            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getTargetFieldName(), conversionEntry.getTargetFieldType());
            // add getter method
            result.addMethod(PoetUtil.obtainGetterMethodsBuilder(conversionEntry.getTargetFieldName(), conversionEntry.getTargetFieldType(), getterSetterMethodNames).build());
            // add setter method
            MethodSpec.Builder setterMethodBuilder = PoetUtil.obtainSetterMethodsBuilderDefault(conversionEntry.getTargetFieldName(), conversionEntry.getTargetFieldType(), getterSetterMethodNames);

            // 数据 setter 绑定关联
            for(HashMap.Entry<String, OOOConversionEntry> ce : oooEntry.getConversions().entrySet()){
                OOOConversionEntry _ce = ce.getValue();
                for(Map.Entry<String, IOOOVariable> variableE : _ce.getInverseBindTargetParamFields().entrySet()){
                    IOOOVariable targetVariable = variableE.getValue();
                    // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                    if(TextUtil.equals(conversionEntry.getTargetFieldName(), targetVariable.fieldName())){

                        String paramsStr = TextUtil.joinHashMap(_ce.getInverseBindTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                            @Override
                            public String call(IOOOVariable ioooTargetVariable) {
                                return ioooTargetVariable.inputCode();
                            }
                        });

                        setterMethodBuilder.addStatement(
                                "$T." + _ce.getInverseBindMethodName() + "(" + paramsStr + ")",
                                _ce.getBindMethodClassType()
                        );
                    }
                }
            }
            result.addMethod(setterMethodBuilder.build());

            // Default extra getter method for Boolean field
            if(ElementUtil.isSameType(conversionEntry.getTargetFieldType(), Boolean.class)){
                result.addMethod(PoetUtil.obtainExtraBooleanGetterMethodsBuilder(conversionEntry.getTargetFieldName(), getterSetterMethodNames).build());
            }
        }
    }

}
