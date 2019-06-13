package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.objs.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOFieldEntry;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class ToMethod1PartBrew implements PartBrew{
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

        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()){
            OOOFieldEntry fieldEntry = fieldE.getValue();
            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(fieldEntry.getSimpleName(), fieldEntry.getTypeName());
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + fieldEntry.getSimpleName() + ")");

        }
        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();
            // TODO: 2019-06-13 wangjie

        }

        result.addMethod(toFromMethod.build());
    }
}
