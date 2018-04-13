package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreTypes;

import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOIgnore;
import com.wangjiegulu.rapidooo.api.OOOs;
import com.wangjiegulu.rapidooo.library.compiler.util.GlobalEnvironment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 12/04/2018.
 */
public class FromEntry {
    private OOOs ooosAnno;

    private String suffix;
    private String fromSuffix;
    private List<String> fromPackages;

    private Element generatorClassEl;
    /**
     * key: pojo class name
     */
    private Map<String, FromElement> allFromElements = new LinkedHashMap<>();

    /**
     * key: @OOO.id
     */
    private Map<String, FromElement> allFromElementIds = new HashMap<>();

    public void setOoosAnno(OOOs ooosAnno) {
        this.ooosAnno = ooosAnno;
    }

    public void parse() {
        suffix = ooosAnno.suffix();
        fromSuffix = ooosAnno.fromSuffix();
        fromPackages = Arrays.asList(ooosAnno.ooosPackages());

        for (String ooosPackage : fromPackages) {
            PackageElement packageElement = GlobalEnvironment.getElementUtils().getPackageElement(ooosPackage);
            if (null == packageElement) {
                throw new RuntimeException("package[" + ooosPackage + "] is not exist.");
//                continue;
            }

            // TODO: 11/04/2018 wangjie
            List<? extends Element> oooClassesElements = packageElement.getEnclosedElements();
            if (null != oooClassesElements) {
                for (Element oooClassElement : oooClassesElements) {
                    if (
                            null != oooClassElement.getAnnotation(OOOs.class)
                                    ||
                                    null != oooClassElement.getAnnotation(OOOIgnore.class)
                            ) {
                        continue;
                    }

                    FromElement fromElement = generateBaseFromElement(oooClassElement);
                    allFromElements.put(MoreTypes.asTypeElement(oooClassElement.asType()).getQualifiedName().toString(), fromElement);
                    fromElement.parse();

                }
            }
        }

        // special ooos
        OOO[] ooos = ooosAnno.ooos();
        for (OOO ooo : ooos) {
            TypeMirror fromTypeMirror = getFromTypeMirror(ooo);
            if (null == fromTypeMirror) {
                continue;
            }
            String specialQualifiedName = MoreTypes.asTypeElement(fromTypeMirror).getQualifiedName().toString();
            FromElement fromElement = allFromElements.get(specialQualifiedName);
            if (null == fromElement) {
                fromElement = generateBaseFromElement(MoreTypes.asElement(fromTypeMirror));
                allFromElements.put(specialQualifiedName, fromElement);
            }

            // cache from element ids
            allFromElementIds.put(ooo.id(), fromElement);

            fromElement.setOooAnno(ooo);
            fromElement.parse();
        }

    }

    private FromElement generateBaseFromElement(Element oooClassElement) {
        FromElement fromElement = new FromElement();
        fromElement.setFromEntry(this);
        fromElement.setElement(oooClassElement);
        fromElement.setGeneratorClassEl(generatorClassEl);
        fromElement.setFromSuffix(fromSuffix);
        fromElement.setSuffix(suffix);
        return fromElement;
    }

    public OOOs getOoosAnno() {
        return ooosAnno;
    }

    public String getSuffix() {
        return suffix;
    }


    public String getFromSuffix() {
        return fromSuffix;
    }


    public List<String> getFromPackages() {
        return fromPackages;
    }


    private static TypeMirror getFromTypeMirror(OOO ooo) {
        try {
            ooo.from();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null;
    }



    public Map<String, FromElement> getAllFromElements() {
        return allFromElements;
    }

    public Element getGeneratorClassEl() {
        return generatorClassEl;
    }

    public void setGeneratorClassEl(Element generatorClassEl) {
        this.generatorClassEl = generatorClassEl;
    }

    public FromElement getFromElementById(String id){
        return allFromElementIds.get(id);
    }

    @Override
    public String toString() {
        return "FromEntry{" +
                "ooosAnno=" + ooosAnno +
                ", suffix='" + suffix + '\'' +
                ", fromSuffix='" + fromSuffix + '\'' +
                ", fromPackages=" + fromPackages +
                ", allFromElements=" + allFromElements +
                '}';
    }
}
