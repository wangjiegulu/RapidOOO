package com.wangjiegulu.rapidooo.library.compiler.part.statement.mto;

import com.squareup.javapoet.MethodSpec;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.OOOConversionEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func1R;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-21.
 */
public class ToMethodStatementUtil {
    public static void buildConversionStatement(MethodSpec.Builder toFromMethod, OOOConversionEntry conversionEntry, String tag) {
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
