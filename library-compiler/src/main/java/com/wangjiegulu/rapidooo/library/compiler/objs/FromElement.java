package com.wangjiegulu.rapidooo.library.compiler.objs;

import java.util.HashMap;

import javax.lang.model.element.Element;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 11/04/2018.
 */
public class FromElement {
    private Element element;
    private String fromSuffix;
    private String suffix;

    /**
     * key: field
     */
    private HashMap<String, FromFieldConversion> specialFromConversions = new HashMap<>();

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getFromSuffix() {
        return fromSuffix;
    }

    public void setFromSuffix(String fromSuffix) {
        this.fromSuffix = fromSuffix;
    }

    public HashMap<String, FromFieldConversion> getSpecialFromConversions() {
        return specialFromConversions;
    }

}
