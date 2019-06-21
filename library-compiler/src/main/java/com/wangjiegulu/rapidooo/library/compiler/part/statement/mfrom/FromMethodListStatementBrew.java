package com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IFromMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class FromMethodListStatementBrew implements IFromMethodStatementBrew {

    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        TypeName typeName = conversionEntry.getTargetFieldType();
        String typeNameStr = typeName.toString();
        // TODO: 2019-06-18 wangjie
        return typeName instanceof ParameterizedTypeName && EasyType.isListType(typeNameStr);
    }

    @Override
    public void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        OOOControlMode controlMode = conversionEntry.getControlMode();
        switch (controlMode) {
            case ATTACH:
                buildAttachStatement(oooEntry, conversionEntry, fromMethodSpec);
                break;
            case BIND:
                buildBindStatement(oooEntry, conversionEntry, fromMethodSpec);
                break;
            case CONVERSION:
                buildConversionStatement(oooEntry, conversionEntry, fromMethodSpec);
                break;
            default:
                LogUtil.logger("[INFO] UNKNOWN Control Mode.");
                break;
        }
    }

    private void buildBindStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        if (conversionEntry.isBindMethodSet()) {
            fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getBindTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    return ioooTargetVariable.inputCode();
                }
            });
            fromMethodSpec.addStatement(
                    "this." + conversionEntry.getTargetFieldName() + " = $T." + conversionEntry.getBindMethodName() + "(" + paramsStr + ")",
                    conversionEntry.getBindMethodClassType()
            );
        }
    }

    private void buildConversionStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        if (conversionEntry.isConversionMethodSet()) {
            fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getConversionTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    return ioooTargetVariable.inputCode();
                }
            });

            fromMethodSpec.addStatement(
                    "this." + conversionEntry.getTargetFieldName() + " = $T." + conversionEntry.getConversionMethodName() + "(" + paramsStr + ")",
                    conversionEntry.getBindMethodClassType()
            );
        }
    }

    private void buildAttachStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + this.getClass().getSimpleName());
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

        ParameterizedTypeName targetFieldType = (ParameterizedTypeName) conversionEntry.getTargetFieldType();
        TypeName targetFieldParamTypeName = targetFieldType.typeArguments.get(0);

        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        // java.util.List<#id__ChatBO>
        if (conversionEntry.getTargetFieldTypeEntry().hasArgumentRefId()) {
            OOOEntry temp = oooEntry.getOoosEntry().queryTypeByName(targetFieldParamTypeName.toString());

            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            fromMethodSpec.addStatement("$T " + attachFieldName + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()", ParameterizedTypeName.get(ClassName.get(List.class), temp.getFromTypeName()));

            fromMethodSpec.addCode(
                    "if(null == " + attachFieldName + "){\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = null;\n" +
                            "} else {\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = new $T<>();\n" +
                            "  for($T item : " + attachFieldName + "){\n" +
                            "    this." + conversionEntry.getTargetFieldName() + ".add($T.create(item));\n" +
                            "  }\n" +
                            "}\n",
                    ArrayList.class, temp.getFromTypeName(), targetFieldParamTypeName);

        } else { // java.util.List<java.lang.String>
            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            fromMethodSpec.addStatement("$T " + attachFieldName + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()", ParameterizedTypeName.get(ClassName.get(List.class), targetFieldParamTypeName));

            fromMethodSpec.addCode(
                    "if(null == " + attachFieldName + "){\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = null;\n" +
                            "} else {\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = new $T<>();\n" +
                            "  for($T item : " + attachFieldName + "){\n" +
                            "    this." + conversionEntry.getTargetFieldName() + ".add(item);\n" +
                            "  }\n" +
                            "}\n",
                    ArrayList.class, targetFieldParamTypeName);
        }
    }


}
