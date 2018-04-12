package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 11/04/2018.
 */
public class FromElement {
    private OOO oooAnno;

    private Element element;
    private String fromSuffix;
    private String suffix;

    /**
     * key: field name
     */
    private Map<String, FromField> allFromFields = new LinkedHashMap<>();


    public void setElement(Element element) {
        this.element = element;
        parse();
    }

    private void parse() {
        List<? extends Element> eles = element.getEnclosedElements();
        for (Element e : eles) {
            if (ElementKind.FIELD == e.getKind()) {
                FromField fromField = new FromField();
                fromField.setFieldOriginElement(e);
                allFromFields.put(e.getSimpleName().toString(), fromField);
            }
        }
    }

    public void setOooAnno(OOO oooAnno) {
        this.oooAnno = oooAnno;
        parseExtra();
    }

    private void parseExtra() {
        String specialSuffix = oooAnno.suffix();
        if (!AnnoUtil.oooParamIsNotSet(specialSuffix)) {
            suffix = specialSuffix;
        }

        String specialFromSuffix = oooAnno.fromSuffix();
        if (!AnnoUtil.oooParamIsNotSet(specialFromSuffix)) {
            fromSuffix = specialFromSuffix;
        }

        OOOConversion[] oooConversions = oooAnno.conversion();
        for (OOOConversion oooConversion : oooConversions) {
            FromFieldConversion fromFieldConversion = new FromFieldConversion();
            fromFieldConversion.setOooConversionAnno(oooConversion);
            String fieldName = oooConversion.fieldName();
            FromField fromField = allFromFields.get(fieldName);
            if (null == fromField) {
                throw new RuntimeException("Field[" + fieldName + "] is not exist.");
            }

            fromField.setFromFieldConversion(fromFieldConversion);
            fromField.parse();
        }

    }


    public Element getElement() {
        return element;
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

    public OOO getOooAnno() {
        return oooAnno;
    }

    public Map<String, FromField> getAllFromFields() {
        return allFromFields;
    }

    public void setAllFromFields(Map<String, FromField> allFromFields) {
        this.allFromFields = allFromFields;
    }
}
