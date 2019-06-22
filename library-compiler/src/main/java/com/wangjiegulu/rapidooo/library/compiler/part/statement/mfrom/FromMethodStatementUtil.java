package com.wangjiegulu.rapidooo.library.compiler.part.statement.mfrom;

import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-21.
 */
public class FromMethodStatementUtil {
    public static void buildBindStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec, String tag) {
        if (conversionEntry.isBindMethodSet()) {
            fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + tag);
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

    public static void buildConversionStatement(OOOEntry oooEntry, OOOConversionEntry conversionEntry, MethodSpec.Builder fromMethodSpec, String tag) {
        if(conversionEntry.isConversionMethodSet()){
            fromMethodSpec.addComment(conversionEntry.getTargetFieldName() + ", " + conversionEntry.getControlMode().getDesc() + ", " + tag);
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
}
