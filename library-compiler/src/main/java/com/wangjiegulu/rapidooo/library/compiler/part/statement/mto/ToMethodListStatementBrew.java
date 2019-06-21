package com.wangjiegulu.rapidooo.library.compiler.part.statement.mto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IToMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import java.util.ArrayList;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ToMethodListStatementBrew implements IToMethodStatementBrew {
    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        TypeName typeName = conversionEntry.getTargetFieldType();
        String typeNameStr = typeName.toString();
        // TODO: 2019-06-18 wangjie
        return typeName instanceof ParameterizedTypeName && EasyType.isListType(typeNameStr);
    }

    @Override
    public void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder toFromMethod) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        // 只有 conversion mode 才需要转换
        OOOControlMode controlMode = conversionEntry.getControlMode();
        switch (controlMode) {
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

    private void buildAttachStatement(OOOEntry oooEntry, String fromParamName, MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());

        ParameterizedTypeName targetFieldType = (ParameterizedTypeName) conversionEntry.getTargetFieldType();
        TypeName targetFieldParamTypeName = targetFieldType.typeArguments.get(0);
        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        // java.util.List<#id__ChatBO>
        if (conversionEntry.getTargetFieldTypeEntry().hasArgumentRefId()) {
            OOOEntry temp = oooEntry.getOoosEntry().queryTypeByName(targetFieldParamTypeName.toString());
            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            toFromMethod.addStatement("List<$T> " + attachFieldName, temp.getFromTypeName());
            toFromMethod.addCode(
                    "if(null == " + conversionEntry.getTargetFieldName() + "){\n" +
                            "  " + attachFieldName + " = null;\n" +
                            "} else {\n" +
                            "  " + attachFieldName + " = new $T<>();\n" +
                            "  for($T item : " + conversionEntry.getTargetFieldName() + "){\n" +
                            "    " + attachFieldName + ".add(item.to$T());\n" +
                            "  }\n" +
                            "}\n" +
                            fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + attachFieldName + ");\n",
                    ArrayList.class, targetFieldParamTypeName, temp.getFromTypeName());
        } else { // java.util.List<java.lang.String>
            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            toFromMethod.addStatement("List<$T> " + attachFieldName, targetFieldParamTypeName);
            toFromMethod.addCode(
                    "if(null == " + conversionEntry.getTargetFieldName() + "){\n" +
                            "  " + attachFieldName + " = null;\n" +
                            "} else {\n" +
                            "  " + attachFieldName + " = new $T<>();\n" +
                            "  for($T item : " + conversionEntry.getTargetFieldName() + "){\n" +
                            "    " + attachFieldName + ".add(item);\n" +
                            "  }\n" +
                            "}\n" +
                            fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + attachFieldName + ");\n",
                    ArrayList.class, targetFieldParamTypeName);
        }
    }

    private void buildConversionStatement(MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        if (conversionEntry.isInverseConversionMethodSet()) {
            toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getInverseConversionTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    return ioooTargetVariable.inputCode();
                }
            });
            toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc());
            toFromMethod.addStatement(
                    "$T." + conversionEntry.getInverseConversionMethodName() + "(" + paramsStr + ")",
                    conversionEntry.getConversionMethodClassType()
            );
        }
    }
}
