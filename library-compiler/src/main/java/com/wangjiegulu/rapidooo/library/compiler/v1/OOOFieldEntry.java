package com.wangjiegulu.rapidooo.library.compiler.v1;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOFieldEntry implements IOOOTargetVariable {
    private TypeName typeName;
    private String simpleName;
    private Modifier[] modifiers;

    public OOOFieldEntry(Element field) {
        typeName = ElementUtil.getTypeName(field);
        simpleName = field.getSimpleName().toString();
        Set<Modifier> modifierSet = field.getModifiers();
        modifiers = new Modifier[modifierSet.size()];
        modifierSet.toArray(modifiers);
    }

    public TypeName getTypeName() {
        return typeName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public Modifier[] getModifiers() {
        return modifiers;
    }

    @Override
    public String fieldName() {
        return simpleName;
    }

    @Override
    public String inputCode() {
        return "this." + simpleName;
    }
}
