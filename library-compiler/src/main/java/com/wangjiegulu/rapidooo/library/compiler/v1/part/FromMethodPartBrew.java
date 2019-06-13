package com.wangjiegulu.rapidooo.library.compiler.v1.part;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.wangjiegulu.rapidooo.library.compiler.objs.GetterSetterMethodNames;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.PoetUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.v1.IOOOTargetVariable;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.v1.OOOFieldEntry;

import java.util.Map;

import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-13.
 */
public class FromMethodPartBrew implements PartBrew{
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
        for (Map.Entry<String, OOOFieldEntry> fieldE : oooEntry.getAllContinuingFields().entrySet()){
            OOOFieldEntry fieldEntry = fieldE.getValue();
            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(fieldEntry.getSimpleName(), fieldEntry.getTypeName());
            fromMethodSpec.addStatement("this." + TextUtil.firstCharLower(fieldEntry.getSimpleName()) + " = " + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "()");
        }
        // Conversion fields
        for (Map.Entry<String, OOOConversionEntry> conversionFieldE : oooEntry.getConversions().entrySet()) {
            OOOConversionEntry conversionEntry = conversionFieldE.getValue();
            // TODO: 2019-06-13 wangjie
            switch(conversionEntry.getControlMode()){
                case BIND:
                    buildBindStatement(oooEntry, conversionEntry, fromMethodSpec);
                    break;
                case CONVERSION:
                    buildConversionStatement(oooEntry, conversionEntry, fromMethodSpec);
                    break;
                case UNKNOWN:
                default:
                    LogUtil.logger("[WARN] UNKNOWN Control Mode.");
                    break;
            }
        }
        result.addMethod(fromMethodSpec.build());
    }

    private void buildBindStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec) {
        if(conversionEntry.isBindMethodSet()){
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getBindTargetParamFields(), ", ", new Func1R<IOOOTargetVariable, String>() {
                @Override
                public String call(IOOOTargetVariable ioooTargetVariable) {
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
        String fromParamName = TextUtil.firstCharLower(oooEntry.getFromSimpleName());
        if(conversionEntry.isConversionMethodSet()){
            GetterSetterMethodNames getterSetterMethodNames = PoetUtil.generateGetterSetterMethodName(conversionEntry.getTargetFieldName(), conversionEntry.getTargetFieldType());
            fromMethodSpec.addStatement(
                    "this." + conversionEntry.getTargetFieldName() + " = $T." + conversionEntry.getConversionMethodName() + "(" + fromParamName + "." + getterSetterMethodNames.getGetterMethodName() + "())",
                    conversionEntry.getBindMethodClassType()
            );
        }
    }


}
