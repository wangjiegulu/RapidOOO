package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.api.OOOControlMode;
import com.wangjiegulu.rapidooo.library.compiler.objs.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.v1.IOOOVariable;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOFieldEntry;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class FromMethodPartBrew implements PartBrew {
    @Override
    public void brew(OOOEntry oooEntry, TypeSpec.Builder result) {
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        MethodSpec.Builder fromMethodSpec = MethodSpec.methodBuilder("from" + oooEntry.getFromSimpleName())
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(oooEntry.getFromTypeName(), fromParamName);

        if (oooEntry.isTargetSupperTypeId()) {
            fromMethodSpec.addStatement("from" + oooEntry.getOoosEntry().getOooGenerator().getOoosEntry().queryTypeIds(oooEntry.getTargetSupperTypeId()).getFromSimpleName()
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
            OOOControlMode controlMode = conversionEntry.getControlMode();
            fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + controlMode.getDesc());
            switch (controlMode) {
                case ATTACH:
                    buildAttachStatement(oooEntry, conversionEntry, fromMethodSpec);
                    break;
                case BIND:
                    buildBindStatement(oooEntry, conversionEntry, fromMethodSpec);
                    break;
                case CONVERSION:
                    // TODO: 2019-06-13 wangjie
                    buildConversionStatement(oooEntry, conversionEntry, fromMethodSpec);
                    break;
                default:
                    LogUtil.logger("[INFO] UNKNOWN Control Mode.");
                    break;
            }
        }
        result.addMethod(fromMethodSpec.build());
    }

    private void buildBindStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        if (conversionEntry.isBindMethodSet()) {
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
        if(conversionEntry.isConversionMethodSet()){
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
        if (conversionEntry.isTargetFieldTypeId()) {
            String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());

            OOOEntry temp = oooEntry.getOoosEntry().queryTypeIds(conversionEntry.getTargetFieldTypeId());
            String tempParam = TextUtil.firstCharLower(temp.getFromSimpleName()) + "_";

            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getAttachFieldName(), conversionEntry.getAttachFieldType());

            fromMethodSpec.addStatement("$T " + tempParam + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()", temp.getFromTypeName());
            fromMethodSpec.addStatement("this." + conversionEntry.getTargetFieldName() + " = null == " + tempParam + " ? null : " + temp.getTargetClassSimpleName() + ".create(" + tempParam + ")");
        }
    }


}
