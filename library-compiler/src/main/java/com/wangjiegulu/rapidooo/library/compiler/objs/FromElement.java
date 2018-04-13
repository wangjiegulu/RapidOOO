package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreElements;

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
    private FromEntry fromEntry;
    private OOO oooAnno;
    private Element generatorClassEl;

    private String targetClassPackage;
    private String targetClassSimpleName;

    private Element element;
    private String fromSuffix;
    private String suffix;

    /**
     * key: field name
     */
    private Map<String, FromField> allFromFields = new LinkedHashMap<>();


    public void setElement(Element element) {
        this.element = element;

        List<? extends Element> eles = element.getEnclosedElements();
        for (Element e : eles) {
            if (ElementKind.FIELD == e.getKind()) {
                FromField fromField = new FromField();
                fromField.setFieldOriginElement(e);
                allFromFields.put(e.getSimpleName().toString(), fromField);
            }
        }
    }

    public void parse() {
        String fromClassName = element.getSimpleName().toString();
        targetClassPackage = generatorClassEl.getEnclosingElement().toString();
        // eg. replace "BO" when generate VO
        targetClassSimpleName =
                (AnnoUtil.oooParamIsNotSet(fromSuffix) ? fromClassName : fromClassName.substring(0, fromClassName.length() - fromSuffix.length()))
                        + suffix;
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
            String fieldName = oooConversion.fieldName();
            FromField fromField = allFromFields.get(fieldName);
            if (null == fromField) {
                throw new RuntimeException("Field[" + fieldName + "] is not exist in " + MoreElements.asType(element).getQualifiedName());
            }

            FromFieldConversion fromFieldConversion = new FromFieldConversion();
            fromFieldConversion.setOwnerFromElement(this);
            fromFieldConversion.setOooConversionAnno(oooConversion);
            fromFieldConversion.setOwnerFromField(fromField);
            fromFieldConversion.parse();
            fromField.setFromFieldConversion(fromFieldConversion);
            fromField.setOwnerFromElement(this);
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

    public Element getGeneratorClassEl() {
        return generatorClassEl;
    }

    public void setGeneratorClassEl(Element generatorClassEl) {
        this.generatorClassEl = generatorClassEl;
    }

    public String getTargetClassPackage() {
        return targetClassPackage;
    }

    public String getTargetClassSimpleName() {
        return targetClassSimpleName;
    }

    public String getTargetClassFullName() {
        return targetClassPackage + "." + targetClassSimpleName;
    }

    public FromEntry getFromEntry() {
        return fromEntry;
    }

    public void setFromEntry(FromEntry fromEntry) {
        this.fromEntry = fromEntry;
    }
}
