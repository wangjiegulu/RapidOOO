package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class ToMethod1PartBrew implements PartBrew {
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

        MethodSpec.Builder toFromMethod = MethodSpec.methodBuilder("to" + oooEntry.getFromSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(oooEntry.getFromTypeName(), fromParamName);

        if (oooEntry.isTargetSupperTypeId()) {
            toFromMethod.addStatement("to" + oooEntry.getOoosEntry().getOooGenerator().getOoosEntry().queryTypeIds(oooEntry.getTargetSupperTypeId()).getFromSimpleName()
                    + "(" + fromParamName + ")");
        }

        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()) {
            OOOFieldEntry fieldEntry = fieldE.getValue();
            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(fieldEntry.getSimpleName(), fieldEntry.getTypeName());
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + fieldEntry.getSimpleName() + ")");

        }

        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();
            // 只有 conversion mode 才需要转换
            switch (conversionEntry.getControlMode()) {
                case ATTACH:
                    buildAttachStatement(oooEntry, fromParamName, toFromMethod, conversionEntry);
                    break;
                case BIND:
                    // ignore
                    break;
                case CONVERSION:
                    buildConversionStatement(toFromMethod, conversionEntry);
                    break;
                default:
                    LogUtil.logger("[INFO] UNKNOWN Control Mode.");
                    break;
            }
        }

        result.addMethod(toFromMethod.build());
    }

    private void buildAttachStatement(OOOEntry oooEntry, String fromParamName, MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        OOOEntry temp = oooEntry.getOoosEntry().queryTypeIds(conversionEntry.getTargetFieldTypeId());

        toFromMethod.addComment(conversionEntry.getTargetFieldName() + conversionEntry.getControlMode().getDesc());
        toFromMethod.addStatement(
                fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + conversionEntry.fieldName() + ".to" + temp.getFromSimpleName() + "())",
                conversionEntry.getConversionMethodClassType()
        );
    }

    private void buildConversionStatement(MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        String paramsStr = TextUtil.joinHashMap(conversionEntry.getInverseConversionTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
            @Override
            public String call(IOOOVariable ioooTargetVariable) {
                return ioooTargetVariable.inputCode();
            }
        });
        toFromMethod.addComment(conversionEntry.getTargetFieldName() + conversionEntry.getControlMode().getDesc());
        toFromMethod.addStatement(
                "$T." + conversionEntry.getInverseConversionMethodName() + "(" + paramsStr + ")",
                conversionEntry.getConversionMethodClassType()
        );
    }
}
