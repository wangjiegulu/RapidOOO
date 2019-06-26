package com.wangjiegulu.rapidooo.library.compiler.part.statement.util;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.func.Func0R;
import com.wangjiegulu.rapidooo.api.func.Func1;
import com.wangjiegulu.rapidooo.api.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-21.
 */
public class ControlModeMethodStatementUtil {
    public static void buildBindStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder methodBuilder, String tag) {
        if (conversionEntry.isBindMethodSet()) {
            methodBuilder.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + tag);
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getBindTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    String prefix = conversionEntry.isControlDelegateSet() ? oooEntry.getTargetClassSimpleName() + "." : "";
                    return prefix + ioooTargetVariable.inputCode();
                }
            });
            if (conversionEntry.isControlDelegateSet()) {
                TypeName targetFieldArgType = conversionEntry.getOooTargetFieldArgTypeEntry().getTypeName();
                TypeName targetFieldType = conversionEntry.getTargetFieldTypeEntry().getTypeName();
                methodBuilder.addCode("new $T<$T>().invoke(new $T<$T>() {\n" +
                                "  @Override\n" +
                                "  public $T call() {\n" +
                                "    return $T." + conversionEntry.getBindMethodName() + "(" + paramsStr + ")" + ";\n" +
                                "  }\n" +
                                "}, new $T<$T>() {\n" +
                                "  @Override\n" +
                                "  public void call($T o) {\n" +
                                "    " + oooEntry.getTargetClassSimpleName() + ".this." + conversionEntry.getTargetFieldName() + " = o;\n" +
                                "  }\n" +
                                "});\n",
                        conversionEntry.getControlDelegateTypeName(),
                        targetFieldArgType,
                        Func0R.class,
                        targetFieldArgType,
                        targetFieldArgType,
                        conversionEntry.getBindMethodClassType(),
                        Func1.class,
                        targetFieldType,
                        targetFieldType
                );
            } else {
                methodBuilder.addStatement(
                        "this." + conversionEntry.getTargetFieldName() + " = $T." + conversionEntry.getBindMethodName() + "(" + paramsStr + ")",
                        conversionEntry.getBindMethodClassType()
                );
            }
        }
    }

    public static void buildInverseBindStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder methodBuilder, String tag) {
        if(conversionEntry.isInverseBindMethodSet()){
            methodBuilder.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + tag);
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getInverseBindTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    return ioooTargetVariable.inputCode();
                }
            });

            methodBuilder.addStatement(
                    "$T." + conversionEntry.getInverseBindMethodName() + "(" + paramsStr + ")",
                    conversionEntry.getBindMethodClassType()
            );
        }
    }

    public static void buildConversionStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder methodBuilder, String tag) {
        if (conversionEntry.isConversionMethodSet()) {
            methodBuilder.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + tag);
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getConversionTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    String prefix = conversionEntry.isControlDelegateSet() ? oooEntry.getTargetClassSimpleName() + "." : "";
                    return prefix + ioooTargetVariable.inputCode();
                }
            });

            if (conversionEntry.isControlDelegateSet()) {
                // TODO: 2019-06-26 wangjie new ControlDelegate
                TypeName targetFieldArgType = conversionEntry.getOooTargetFieldArgTypeEntry().getTypeName();
                TypeName targetFieldType = conversionEntry.getTargetFieldTypeEntry().getTypeName();
                methodBuilder.addCode("new $T<$T>().invoke(new $T<$T>() {\n" +
                                "  @Override\n" +
                                "  public $T call() {\n" +
                                "    return $T." + conversionEntry.getConversionMethodName() + "(" + paramsStr + ")" + ";\n" +
                                "  }\n" +
                                "}, new $T<$T>() {\n" +
                                "  @Override\n" +
                                "  public void call($T o) {\n" +
                                "    " + oooEntry.getTargetClassSimpleName() + ".this." + conversionEntry.getTargetFieldName() + " = o;\n" +
                                "  }\n" +
                                "});\n",
                        conversionEntry.getControlDelegateTypeName(),
                        targetFieldArgType,
                        Func0R.class,
                        targetFieldArgType,
                        targetFieldArgType,
                        conversionEntry.getBindMethodClassType(),
                        Func1.class,
                        targetFieldType,
                        targetFieldType
                );
            } else {
                methodBuilder.addStatement(
                        "this." + conversionEntry.getTargetFieldName() + " = $T." + conversionEntry.getConversionMethodName() + "(" + paramsStr + ")",
                        conversionEntry.getBindMethodClassType()
                );
            }
        }
    }

    public static void buildInverseConversionStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder toFromMethod, String tag) {
        if (conversionEntry.isInverseConversionMethodSet()) {
            toFromMethod.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + tag);
            String paramsStr = TextUtil.joinHashMap(conversionEntry.getInverseConversionTargetParamFields(), ", ", new Func1R<IOOOVariable, String>() {
                @Override
                public String call(IOOOVariable ioooTargetVariable) {
                    return ioooTargetVariable.inputCode();
                }
            });
            toFromMethod.addStatement(
                    "$T." + conversionEntry.getInverseConversionMethodName() + "(" + paramsStr + ")",
                    conversionEntry.getConversionMethodClassType()
            );
        }
    }
}
