package com.wangjiegulu.rapidooo.library.compiler.part.impl;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.PartBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IFromMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom.FromMethodListStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom.FromMethodObjectStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class FromMethodPartBrew implements PartBrew {
    private List<IFromMethodStatementBrew> statementBrews = new ArrayList<>();
    public FromMethodPartBrew() {
        statementBrews.add(new FromMethodObjectStatementBrew());
        statementBrews.add(new FromMethodListStatementBrew());
    }

    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        MethodSpec.Builder fromMethodSpec = MethodSpec.methodBuilder("from" + oooEntry.getFromSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(oooEntry.getFromTypeName(), fromParamName);

        if (oooEntry.isTargetSupperTypeId()) {
            fromMethodSpec.addStatement("from" + oooEntry.getOoosEntry().getOooGenerator().getOoosEntry().queryTypeById(oooEntry.getTargetSupperTypeId()).getFromSimpleName()
                    + "(" + fromParamName + ")");
        }

        // Continuing fields
        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()) {
            OOOFieldEntry fieldEntry = fieldE.getValue();
            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(fieldEntry.getSimpleName(), fieldEntry.getTypeName());
            fromMethodSpec.addStatement("this." + TextUtil.firstCharLower(fieldEntry.getSimpleName()) + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()");
        }
        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();

            for(IFromMethodStatementBrew brew : statementBrews){
                if(brew.match(conversionEntry)){
                    brew.buildStatement(oooEntry, conversionEntry, fromMethodSpec);
                    break;
                }
            }
        }
        result.addMethod(fromMethodSpec.build());
    }

}
