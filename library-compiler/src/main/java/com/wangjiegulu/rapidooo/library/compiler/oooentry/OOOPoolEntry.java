package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOPool;
import com.wangjiegulu.rapidooo.library.compiler.exception.RapidOOOCompileException;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.TextUtil;
import com.wangjiegulu.rapidooo.api.func.Func0R;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOPoolEntry {
    private OOOEntry oooEntry;

    private TypeMirror poolMethodClass;
    private TypeName poolMethodClassTypeName;
    private String acquireMethod;
    private String releaseMethod;

    private boolean isPoolUsed;

    public OOOPoolEntry(OOOEntry oooEntry, final OOOPool oooPool) {
        this.oooEntry = oooEntry;

        poolMethodClass = AnnoUtil.getType(new Func0R<Object>() {
            @Override
            public Object call() {
                return oooPool.poolMethodClass();
            }
        });
        poolMethodClassTypeName = ElementUtil.getTypeName(poolMethodClass);
        if (ElementUtil.isSameType(poolMethodClassTypeName, TypeName.get(Object.class))) {
            poolMethodClassTypeName = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassType();
            poolMethodClass = oooEntry.getOoosEntry().getOooGenerator().getGeneratorClassEl().asType();
        }

        acquireMethod = oooPool.acquireMethod();
        releaseMethod = oooPool.releaseMethod();

    }

    public void parse() {
        boolean acquireMethodSet = !AnnoUtil.oooParamIsNotSet(acquireMethod);
        boolean releaseMethodSet = !AnnoUtil.oooParamIsNotSet(releaseMethod);

        if (!acquireMethodSet && !releaseMethodSet) {
            isPoolUsed = false;
        } else if (acquireMethodSet && releaseMethodSet) {

            ExecutableElement acquireMethodEle = findAcquireMethodInClass();
            if (null == acquireMethodEle) {
                throw new RapidOOOCompileException("Method[public static " + oooEntry.getTargetClassType() + " " + acquireMethod + "()] not found \nin " + poolMethodClass + " class");
            }

            ExecutableElement releaseMethodEle = findReleaseMethodInClass();
            if (null == releaseMethodEle) {
                throw new RapidOOOCompileException("Method[public static void " + releaseMethod + "(" + oooEntry.getTargetClassType() + ")] not found \nin " + poolMethodClass + " class");
            }

            isPoolUsed = true;
        } else {
            throw new RapidOOOCompileException("Both `AcquireMethod` and `ReleaseMethodSet` need to be set.");
        }
    }

    private ExecutableElement findAcquireMethodInClass() {
        for (Element e : MoreTypes.asElement(poolMethodClass).getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                // Must be public static
                ExecutableElement method = MoreElements.asExecutable(e);
                if (!MoreElements.hasModifiers(Modifier.STATIC).apply(method)
                        ||
                        !MoreElements.hasModifiers(Modifier.PUBLIC).apply(method)
                ) {
                    continue;
                }
                if (TextUtil.equals(acquireMethod, method.getSimpleName().toString())
                                &&
                                // TODO: 2019-06-14 wangjie validate class type simple name here, need optimize
                                TextUtil.equals(method.getReturnType().toString(), oooEntry.getTargetClassSimpleName())
                                &&
                                TextUtil.isEmpty(method.getParameters())
                ) {
                    return method;
                }
            }
        }
        return null;
    }

    private ExecutableElement findReleaseMethodInClass() {
        for (Element e : MoreTypes.asElement(poolMethodClass).getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                // Must be public static
                ExecutableElement method = MoreElements.asExecutable(e);
                if (!MoreElements.hasModifiers(Modifier.STATIC).apply(method)
                        ||
                        !MoreElements.hasModifiers(Modifier.PUBLIC).apply(method)
                ) {
                    continue;
                }
                if (TextUtil.equals(releaseMethod, method.getSimpleName().toString())
                                &&
                                TextUtil.equals(method.getReturnType().toString(), "void")
                                &&
                                TextUtil.size(method.getParameters()) == 1
                                &&
                                // TODO: 2019-06-14 wangjie validate class type simple name here, need optimize
                                TextUtil.equals(method.getParameters().get(0).asType().toString(), oooEntry.getTargetClassSimpleName())
                ) {
                    return method;
                }
            }
        }
        return null;
    }

    public OOOEntry getOooEntry() {
        return oooEntry;
    }

    public TypeName getPoolMethodClassTypeName() {
        return poolMethodClassTypeName;
    }

    public String getAcquireMethod() {
        return acquireMethod;
    }

    public String getReleaseMethod() {
        return releaseMethod;
    }

    public boolean isPoolUsed() {
        return isPoolUsed;
    }

}
