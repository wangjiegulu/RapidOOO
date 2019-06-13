package com.wangjiegulu.rapidooo.library.compiler.v1;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOOs;

import javax.lang.model.element.Element;

/**
 * Author: wangjie Email: tiantian.china.2@gmail.com Date: 2019-06-12.
 */
public class OOOGenerator {
    private Element generatorClassEl;
    private TypeName generatorClassType;
    private OOOSEntry ooosEntry;

    public OOOGenerator(Element generatorClassEl) {
        this.generatorClassEl = generatorClassEl;
        generatorClassType = TypeName.get(generatorClassEl.asType());
    }

    public void parse() {
        OOOs ooos = generatorClassEl.getAnnotation(OOOs.class);

        ooosEntry = new OOOSEntry(this, ooos).prepare();
        ooosEntry.parse();

    }


    public Element getGeneratorClassEl() {
        return generatorClassEl;
    }

    public TypeName getGeneratorClassType() {
        return generatorClassType;
    }

    public OOOSEntry getOoosEntry() {
        return ooosEntry;
    }

    @Override
    public String toString() {
        return "OOOGenerator{" +
                "generatorClassEl=" + generatorClassEl +
                ", generatorClassType=" + generatorClassType +
                ", ooosEntry=" + ooosEntry +
                '}';
    }
}
