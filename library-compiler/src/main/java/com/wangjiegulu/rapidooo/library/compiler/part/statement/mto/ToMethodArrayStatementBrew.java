package com.wangjiegulu.rapidooo.library.compiler.part.statement.mto;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IToMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ToMethodArrayStatementBrew implements IToMethodStatementBrew {
    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        return conversionEntry.getTargetFieldTypeEntry().isArray();
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
                break;
        }
    }

    private void buildAttachStatement(OOOEntry oooEntry, String fromParamName, MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());

        ArrayTypeName targetFieldType = (ArrayTypeName) conversionEntry.getTargetFieldType();
        TypeName targetFieldParamTypeName = targetFieldType.componentType;
        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        OOOEntry temp = OOOSEntry.queryTypeByName(targetFieldParamTypeName.toString());
        // #id__ChatBO[]
        if (null != temp && !ElementUtil.isSameType(temp.getFromTypeName(), targetFieldParamTypeName)) {
            String attachFieldName = conversionEntry.getAttachFieldName() + "_";

            toFromMethod.addCode(
                    "$T[] " + attachFieldName + ";\n" +
                            "if(null == this." + conversionEntry.getTargetFieldName() + "){\n" +
                            "  " + attachFieldName + " = null;\n" +
                            "} else {\n" +
                            "  " + attachFieldName + " = new $T[" + conversionEntry.getTargetFieldName() + ".length];\n" +
                            "  for(int i = 0, len = this." + conversionEntry.getTargetFieldName() + ".length; i < len; i++){\n" +
                            "    " + attachFieldName + "[i] = this." + conversionEntry.getTargetFieldName() + "[i].to" + temp.getFromSimpleName() + "();\n" +
                            "  }\n" +
                            "}\n",
                    temp.getFromTypeName(), temp.getFromTypeName()
            );
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + attachFieldName + ")");
        } else { // java.lang.String[]
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(this." + conversionEntry.getTargetFieldName() + ")");
        }
    }


}
