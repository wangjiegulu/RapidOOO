package com.wangjiegulu.rapidooo.library.compiler.base.contract;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class ElementStuff implements IElementStuff {
    private Element element;

    public ElementStuff(Element element) {
        this.element = element;
    }

    @Override
    public TypeName asType() {
        return ClassName.get(element.asType());
    }

    @Override
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }
}
