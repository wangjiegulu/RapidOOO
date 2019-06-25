package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntry;
import com.wangjiegulu.rapidooo.library.compiler.oooentry.type.OOOTypeEntryFactory;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOFieldEntry implements IOOOVariable{
    private TypeName typeName;
    private String simpleName;
    private Modifier[] modifiers;
    private boolean parcelable;
    private boolean classFound;

    private OOOTypeEntry oooTypeEntry;

    public OOOFieldEntry(Element field) {
        // TODO: 2019-06-25 wangjie optimize: classNotFound
        try {
            typeName = ElementUtil.getTypeName(field);
            classFound = true;
        } catch (Throwable throwable) {
            LogUtil.logger("[WARN]" + throwable.getMessage());
            typeName = EasyType.parseTypeName(field.asType().toString());
        }
        simpleName = field.getSimpleName().toString();
        Set<Modifier> modifierSet = field.getModifiers();
        modifiers = new Modifier[modifierSet.size()];
        modifierSet.toArray(modifiers);

        if (classFound) {
            parcelable = ElementUtil.isParcelable(field.asType());
        } else {
            parcelable = false;
        }

        oooTypeEntry = OOOTypeEntryFactory.create(typeName);
        LogUtil.logger("---------> " + field + ",       typeName: " + typeName + ",         parcelable: " + parcelable);
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

    public OOOTypeEntry getOooTypeEntry() {
        return oooTypeEntry;
    }

    public boolean isParcelable() {
        return parcelable;
    }

    public boolean isClassFound() {
        return classFound;
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
