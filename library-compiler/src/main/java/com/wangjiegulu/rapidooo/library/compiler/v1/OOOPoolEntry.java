package com.wangjiegulu.rapidooo.library.compiler.v1;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOPool;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.func.Func0R;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOPoolEntry {
    private OOOEntry oooEntry;

    private TypeName poolMethodClass;
    private String acquireMethod;
    private String releaseMethod;

    public OOOPoolEntry(OOOEntry oooEntry, final OOOPool oooPool) {
        this.oooEntry = oooEntry;

        poolMethodClass = ElementUtil.getTypeName(AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return oooPool.poolMethodClass();
            }
        }));
        if(ElementUtil.isSameType(poolMethodClass, TypeName.get(Object.class))){
            poolMethodClass = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType();
        }

        acquireMethod = oooPool.acquireMethod();
        releaseMethod = oooPool.releaseMethod();

    }

    public void parse() {

    }

    public OOOEntry getOooEntry() {
        return oooEntry;
    }

    public TypeName getPoolMethodClassType() {
        return poolMethodClass;
    }

    public String getAcquireMethod() {
        return acquireMethod;
    }

    public String getReleaseMethod() {
        return releaseMethod;
    }

    @Override
    public String toString() {
        return "OOOPoolEntry{" +
                ", poolMethodClass=" + poolMethodClass +
                ", acquireMethod='" + acquireMethod + '\'' +
                ", releaseMethod='" + releaseMethod + '\'' +
                '}';
    }
}
