package com.wangjiegulu.rapidooo.library.compiler.oooentry;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.library.compiler.part.statement.contact.ParcelableEntry;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;
import com.wangjiegulu.rapidooo.library.compiler.variables.IOOOVariable;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOFieldEntry implements IOOOVariable, ParcelableEntry {
    private TypeName typeName;
    private String simpleName;
    private Modifier[] modifiers;
    private boolean parcelable;

    private OOOTypeEntry oooTypeEntry;

    public OOOFieldEntry(Element field) {
        typeName = ElementUtil.getTypeName(field);
        simpleName = field.getSimpleName().toString();
        Set<Modifier> modifierSet = field.getModifiers();
        modifiers = new Modifier[modifierSet.size()];
        modifierSet.toArray(modifiers);
        parcelable = ElementUtil.isParcelable(field);

        oooTypeEntry = new OOOTypeEntry();
        oooTypeEntry.parse(typeName);

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

    @Override
    public boolean isParcelable() {
        return parcelable;
    }

    @Override
    public String fieldName() {
        return simpleName;
    }

    @Override
    public OOOTypeEntry fieldTypeEntry() {
        return oooTypeEntry;
    }

    @Override
    public String inputCode() {
        return "this." + simpleName;
    }
}
