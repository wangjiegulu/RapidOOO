package com.wangjiegulu.rapidooo.library.compiler.part.statement.mto;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IToMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.util.ControlModeMethodStatementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ToMethodObjectStatementBrew implements IToMethodStatementBrew {
    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        return conversionEntry.getTargetFieldType() instanceof ClassName;
    }

    @Override
    public void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder toFromMethod) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        OOOControlMode controlMode = conversionEntry.getControlMode();
        switch (controlMode) {
            case ATTACH:
                buildAttachStatement(oooEntry, fromParamName, toFromMethod, conversionEntry);
                break;
            case BIND:
                // ignore
                break;
            case CONVERSION:
                ControlModeMethodStatementUtil.buildInverseConversionStatement(oooEntry, conversionEntry, toFromMethod, this.getClass().getSimpleName());
                break;
            default:
                break;
        }
    }

    private void buildAttachStatement(OOOEntry oooEntry, String fromParamName, MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());

        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        // #id_ChatBO
        if (conversionEntry.getTargetFieldTypeEntry().isRefId()) {

            OOOEntry temp = OOOSEntry.queryTypeById(conversionEntry.getTargetFieldTypeId());

//            toFromMethod.addStatement(
//                    fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + conversionEntry.fieldName() + ".to" + temp.getFromSimpleName() + "())",
//                    conversionEntry.getConversionMethodClassType()
//            );

            toFromMethod.addStatement(
                    fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(null == " + conversionEntry.fieldName() + " ? null : " + conversionEntry.fieldName() + ".to" + temp.getFromSimpleName() + "())",
                    conversionEntry.getConversionMethodClassType()
            );

        } else { // String.class
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + conversionEntry.getTargetFieldName() + ")");
        }
    }

}
