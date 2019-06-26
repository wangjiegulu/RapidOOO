package com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom;

import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IFromMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.util.ControlModeMethodStatementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-21.
 */
public class FromMethodArrayStatementBrew implements IFromMethodStatementBrew {
    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        return conversionEntry.getTargetFieldTypeEntry().isArray();
    }

    @Override
    public void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        OOOControlMode controlMode = conversionEntry.getControlMode();
        switch (controlMode) {
            case ATTACH:
                buildAttachStatement(oooEntry, conversionEntry, fromMethodSpec);
                break;
            case BIND:
                ControlModeMethodStatementUtil.buildBindStatement(oooEntry, conversionEntry, fromMethodSpec, this.getClass().getSimpleName());
                break;
            case CONVERSION:
                ControlModeMethodStatementUtil.buildConversionStatement(oooEntry, conversionEntry, fromMethodSpec, this.getClass().getSimpleName());
                break;
            default:
                break;
        }
    }


    private void buildAttachStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

        ArrayTypeName targetFieldType = (ArrayTypeName) conversionEntry.getTargetFieldType();
        TypeName targetFieldParamTypeName = targetFieldType.componentType;

        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        OOOEntry temp = OOOSEntry.queryTypeByName(targetFieldParamTypeName.toString());
        // #id__ChatBO[]
        if (null != temp && !ElementUtil.isSameType(temp.getFromTypeName(), targetFieldParamTypeName)) {
            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            fromMethodSpec.addStatement("$T[] " + attachFieldName + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()", temp.getFromTypeName());

            fromMethodSpec.addCode(
                    "if(null == " + attachFieldName + "){\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = null;\n" +
                            "} else {\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = new $T[" + attachFieldName + ".length];\n" +
                            "  for(int i = 0, len = " + attachFieldName + ".length; i < len; i++){\n" +
                            "    this." + conversionEntry.getTargetFieldName() + "[i] = $T.create(" + attachFieldName + "[i]);\n" +
                            "  }\n" +
                            "}\n",
                    targetFieldParamTypeName, targetFieldParamTypeName);
        } else { // java.lang.String[]
            fromMethodSpec.addStatement("this." + conversionEntry.getTargetFieldName() + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()");
        }
    }
}
