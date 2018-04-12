package com.wangjiegulu.rapidooo.library.compiler.base.contract;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

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
    public TypeMirror asType() {
        return element.asType();
    }

    @Override
    public String getSimpleName() {
        return element.getSimpleName().toString();
    }
}
