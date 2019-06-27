package com.wangjiegulu.rapidooo.library.compiler.part.statement.mto;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOFieldMode;
import com.wangjiegulu.rapidooo.library.compiler.entry.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOFieldEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOSEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOMapTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.IToMethodStatementBrew;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.util.FieldModeMethodStatementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-18.
 */
public class ToMethodMapStatementBrew implements IToMethodStatementBrew {
    @Override
    public boolean match(OOOConversionEntry conversionEntry) {
        return conversionEntry.getTargetFieldTypeEntry().isMap();
    }

    @Override
    public void buildStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder toFromMethod) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        // 只有 conversion mode 才需要转换
        OOOFieldMode fieldMode = conversionEntry.getFieldMode();
        switch (fieldMode) {
            case ATTACH:
                buildAttachStatement(oooEntry, fromParamName, toFromMethod, conversionEntry);
                break;
            case BIND:
                // ignore
                break;
            case CONVERSION:
                FieldModeMethodStatementUtil.buildInverseConversionStatement(oooEntry, conversionEntry, toFromMethod, this.getClass().getSimpleName());
                break;
            default:
                break;
        }
    }

    private void buildAttachStatement(OOOEntry oooEntry, String fromParamName, MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry) {
        toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getFieldMode().getDesc() + ", " + this.getClass().getSimpleName());

        GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

        OOOFieldEntry fromFieldEntry = conversionEntry.getAttachFieldEntry();
        OOOTypeEntry fromFieldTypeEntry = fromFieldEntry.getOooTypeEntry();

        if (ElementUtil.isSameType(fromFieldTypeEntry.getTypeName(), conversionEntry.getTargetFieldType())) {
            toFromMethod.addStatement(fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(this." + conversionEntry.getTargetFieldName() + ")");
        } else {

            TypeName targetFieldKeyTypeName = ((OOOMapTypeEntry) conversionEntry.getTargetFieldTypeEntry()).getKeyTypeName();
            TypeName targetFieldValueTypeName = ((OOOMapTypeEntry) conversionEntry.getTargetFieldTypeEntry()).getValueTypeName();

            TypeName fromFieldKeyTypeName = ((OOOMapTypeEntry) fromFieldTypeEntry).getKeyTypeName();
            TypeName fromFieldValueTypeName = ((OOOMapTypeEntry) fromFieldTypeEntry).getValueTypeName();

            boolean sameKeyType = ElementUtil.isSameType(targetFieldKeyTypeName, fromFieldKeyTypeName);
            boolean sameValueType = ElementUtil.isSameType(targetFieldValueTypeName, fromFieldValueTypeName);

            String attachFieldName = conversionEntry.getAttachFieldName() + "_";
            toFromMethod.addStatement("$T " + attachFieldName, fromFieldTypeEntry.getTypeName());

            List<Object> arguments = new ArrayList<>();
            arguments.add(((OOOMapTypeEntry) fromFieldTypeEntry).getRawType());
            arguments.add(Map.class);
            arguments.add(targetFieldKeyTypeName);
            arguments.add(targetFieldValueTypeName);
            if(!sameKeyType){
                if(null == OOOSEntry.queryTypeByName(targetFieldKeyTypeName.toString())){
                    throw new RapidOOOCompileException("ERROR Field Conversion: " + conversionEntry.getTargetFieldName() + "\nIn " + oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType());
                }
                arguments.add(fromFieldKeyTypeName);
            }
            if(!sameValueType){
                if(null == OOOSEntry.queryTypeByName(targetFieldValueTypeName.toString())){
                    throw new RapidOOOCompileException("ERROR Field Conversion: " + conversionEntry.getTargetFieldName() + "\nIn " + oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType());
                }
                arguments.add(fromFieldValueTypeName);
            }

            toFromMethod.addCode(
                    "if(null == this." + conversionEntry.getTargetFieldName() + "){\n" +
                            "  " + attachFieldName + " = null;\n" +
                            "} else {\n" +
                            "  " + attachFieldName + " = new $T<>();\n" +
                            "  for($T.Entry<$T, $T> entry : this." + conversionEntry.getTargetFieldName() + ".entrySet()){\n" +
                            "    " + attachFieldName
                            + ".put(" + (sameKeyType ? "entry.getKey()" : "entry.getKey().to$T()") + ", " + (sameValueType ? "entry.getValue()" : "entry.getValue().to$T()") + ");\n" +
                            "  }\n" +
                            "}\n" +
                            fromParamName + "." + getterSetterMethodNames.getSetterMethodName() + "(" + attachFieldName + ");\n",
                    arguments.toArray(new Object[arguments.size()]));

        }
    }

}
