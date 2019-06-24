package com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IFromMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class FromMethodObjectStatementBrew implements IFromMethodStatementBrew {

    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
//        LogUtil.logger(")))))))))conversionEntry.getTargetFieldType().getClass(): " + conversionEntry.getTargetFieldType().getClass());
//        LogUtil.logger(")))))))))ClassName.class: " + ClassName.class);
//        LogUtil.logger(")))))))))conversionEntry.getTargetFieldType(): " + conversionEntry.getTargetFieldType());
        return conversionEntry.getTargetFieldType().getClass() == ClassName.class;
    }

    @Override
    public void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        OOOControlMode controlMode = conversionEntry.getControlMode();
        switch (controlMode) {
            case ATTACH:
                buildAttachStatement(oooEntry, conversionEntry, fromMethodSpec);
                break;
            case BIND:
                FromMethodStatementUtil.buildBindStatement(oooEntry, conversionEntry, fromMethodSpec, this.getClass().getSimpleName());
                break;
            case CONVERSION:
                FromMethodStatementUtil.buildConversionStatement(oooEntry, conversionEntry, fromMethodSpec, this.getClass().getSimpleName());
                break;
            default:
                LogUtil.logger("[INFO] UNKNOWN Control Mode.");
                break;
        }
    }

    private void buildAttachStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());
        // #id_ChatBO
        if(conversionEntry.getTargetFieldTypeEntry().isRefId()){
            String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

            OOOEntry temp = OOOSEntry.queryTypeById(conversionEntry.getTargetFieldTypeId());
            String tempParam = TextUtil.firstCharLower(temp.getFromSimpleName()) + "_";

            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

            fromMethodSpec.addStatement("$T " + tempParam + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()", temp.getFromTypeName());
            fromMethodSpec.addStatement("this." + conversionEntry.getTargetFieldName() + " = null == " + tempParam + " ? null : " + temp.getTargetClassSimpleName() + ".create(" + tempParam + ")");
        } else{ // String.class
            String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());
            fromMethodSpec.addStatement("this." + conversionEntry.getTargetFieldName() + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()", conversionEntry.getAttachFieldType());
        }

    }


}
