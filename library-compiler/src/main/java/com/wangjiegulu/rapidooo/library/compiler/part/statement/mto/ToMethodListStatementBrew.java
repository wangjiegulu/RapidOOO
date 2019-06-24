package com.wangjiegulu.rapidooo.library.compiler.part.statement.mto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IToMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

import java.util.ArrayList;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ToMethodListStatementBrew implements IToMethodStatementBrew {
    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        return conversionEntry.getTargetFieldTypeEntry().isList();
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
                ToMethodStatementUtil.buildConversionStatement(toFromMethod, conversionEntry, this.getClass().getSimpleName());
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

        OOOEntry temp = OOOSEntry.queryTypeByName(targetFieldParamTypeName.toString());

        // java.util.List<#id__ChatBO>
        if (null != temp && !ElementUtil.isSameType(temp.getFromTypeName(), targetFieldParamTypeName)) {
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
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(this." + conversionEntry.getTargetFieldName() + ")");
        }
    }

}
