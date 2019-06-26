package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IToMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.mto.ToMethodArrayStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.mto.ToMethodListStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.mto.ToMethodMapStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.mto.ToMethodObjectStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class ToMethod1PartBrew implements PartBrew {
    private List<IToMethodStatementBrew> statementBrews = new ArrayList<>();
    public ToMethod1PartBrew() {
        statementBrews.add(new ToMethodListStatementBrew());
        statementBrews.add(new ToMethodArrayStatementBrew());
        statementBrews.add(new ToMethodMapStatementBrew());
        statementBrews.add(new ToMethodObjectStatementBrew());
    }

    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

        MethodSpec.Builder toFromMethod = MethodSpec.methodBuilder("to" + oooEntry.getFromSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(oooEntry.getFromTypeName(), fromParamName);

        if (oooEntry.isTargetSupperTypeId()) {
            toFromMethod.addStatement("to" + OOOSEntry.queryTypeById(oooEntry.getTargetSupperTypeId()).getFromSimpleName()
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

            for(IToMethodStatementBrew brew : statementBrews){
                if(brew.match(conversionEntry)){
                    brew.buildStatement(oooEntry, conversionEntry, toFromMethod);
                    break;
                }
            }

        }

        result.addMethod(toFromMethod.build());
    }


}
