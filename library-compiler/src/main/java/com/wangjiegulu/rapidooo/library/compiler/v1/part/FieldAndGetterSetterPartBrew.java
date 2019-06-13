package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.objs.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.v1.IOOOTargetVariable;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOFieldEntry;

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
                    .addJavadoc("field name: {@link $T#$L}\n",
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
                OOOConversionEntry _ce = ce.getValue();
                for(Map.Entry<String, IOOOTargetVariable> variableE : _ce.getBindTargetParamFields().entrySet()){
                    IOOOTargetVariable targetVariable = variableE.getValue();
                    // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                    if(TextUtil.equals(fieldEntry.getSimpleName(), targetVariable.fieldName())){

                        String paramsStr = TextUtil.joinHashMap(_ce.getBindTargetParamFields(), ", ", new Func1R<IOOOTargetVariable, String>() {
                            @Override
                            public String call(IOOOTargetVariable ioooTargetVariable) {
                                return ioooTargetVariable.inputCode();
                            }
                        });

                        setterMethodBuilder.addStatement(
                                "this." + _ce.getTargetFieldName() + " = $T." + _ce.getBindMethodName() + "(" + paramsStr + ")",
                                _ce.getBindMethodClassType()
                        );
                    }
                }
            }
            result.addMethod(setterMethodBuilder.build());

        }
    }

    private void buildConversionsFields(OOOEntry oooEntry, TypeSpec.Builder result) {
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()){
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();
            FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(conversionEntry.getTargetFieldType(), conversionEntry.getTargetFieldName(), Modifier.PRIVATE)
                    .addJavadoc("field name conversion : {@link $T}\n",
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
                for(Map.Entry<String, IOOOTargetVariable> variableE : _ce.getInverseBindTargetParamFields().entrySet()){
                    IOOOTargetVariable targetVariable = variableE.getValue();
                    // 该字段被某个 conversion 的 bind 方法作为参数使用到，则需要绑定
                    if(TextUtil.equals(conversionEntry.getTargetFieldName(), targetVariable.fieldName())){

                        String paramsStr = TextUtil.joinHashMap(_ce.getInverseBindTargetParamFields(), ", ", new Func1R<IOOOTargetVariable, String>() {
                            @Override
                            public String call(IOOOTargetVariable ioooTargetVariable) {
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

        }
    }
}
