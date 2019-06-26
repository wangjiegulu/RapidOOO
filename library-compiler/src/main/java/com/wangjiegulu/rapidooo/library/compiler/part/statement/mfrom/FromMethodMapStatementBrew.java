package com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOMapTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IFromMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.util.ControlModeMethodStatementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class FromMethodMapStatementBrew implements IFromMethodStatementBrew {

    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        return conversionEntry.getTargetFieldTypeEntry().isMap();
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

        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        OOOFieldEntry fromFieldEntry = conversionEntry.getAttachFieldEntry();
        OOOTypeEntry fromFieldTypeEntry = fromFieldEntry.getOooTypeEntry();

        if (ElementUtil.isSameType(fromFieldTypeEntry.getTypeName(), conversionEntry.getTargetFieldType())) {
            fromMethodSpec.addStatement("this." + conversionEntry.getTargetFieldName() + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()");
        } else {

            TypeName targetFieldKeyTypeName = ((OOOMapTypeEntry) conversionEntry.getTargetFieldTypeEntry()).getKeyTypeName();
            TypeName targetFieldValueTypeName = ((OOOMapTypeEntry) conversionEntry.getTargetFieldTypeEntry()).getValueTypeName();

            TypeName fromFieldKeyTypeName = ((OOOMapTypeEntry) fromFieldTypeEntry).getKeyTypeName();
            TypeName fromFieldValueTypeName = ((OOOMapTypeEntry) fromFieldTypeEntry).getValueTypeName();

            boolean sameKeyType = ElementUtil.isSameType(targetFieldKeyTypeName, fromFieldKeyTypeName);
            boolean sameValueType = ElementUtil.isSameType(targetFieldValueTypeName, fromFieldValueTypeName);

            TypeName targetRawType = ((OOOMapTypeEntry) conversionEntry.getTargetFieldTypeEntry()).getRawType();

            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            fromMethodSpec.addStatement("$T " + attachFieldName + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()",
                    fromFieldTypeEntry.getTypeName());

            List<Object> arguments = new ArrayList<>();
            arguments.add(targetRawType);
            arguments.add(Map.class);
            arguments.add(fromFieldKeyTypeName);
            arguments.add(fromFieldValueTypeName);
            if(!sameKeyType){
                if(null == OOOSEntry.queryTypeByName(targetFieldKeyTypeName.toString())){
                    throw new RapidOOOCompileException("ERROR Field Conversion: " + conversionEntry.getTargetFieldName() + "\nIn " + oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType());
                }
                arguments.add(targetFieldKeyTypeName);
            }
            if(!sameValueType){
                if(null == OOOSEntry.queryTypeByName(targetFieldValueTypeName.toString())){
                    throw new RapidOOOCompileException("ERROR Field Conversion: " + conversionEntry.getTargetFieldName() + "\nIn " + oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType());
                }
                arguments.add(targetFieldValueTypeName);
            }

            fromMethodSpec.addCode(
                    "if(null == " + attachFieldName + "){\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = null;\n" +
                            "} else {\n" +
                            "  this." + conversionEntry.getTargetFieldName() + " = new $T<>();\n" +
                            "  for($T.Entry<$T, $T> entry : " + attachFieldName + ".entrySet()){\n" +
                            "    this." + conversionEntry.getTargetFieldName()
                            + ".put(" + (sameKeyType ? "entry.getKey()" : "$T.create(entry.getKey())") + ", " + (sameValueType ? "entry.getValue()" : "$T.create(entry.getValue())") + ");\n" +
                            "  }\n" +
                            "}\n",
                    arguments.toArray(new Object[arguments.size()]));

        }


    }


}
