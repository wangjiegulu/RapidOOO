package com.wangjiegulu.rapidooo.library.compiler.objs;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;

import com.squareup.javapoet.TypeName;
import com.wangjiegulu.rapidooo.api.OOO;
import com.wangjiegulu.rapidooo.api.OOOConversion;
import com.wangjiegulu.rapidooo.api.OOOPool;
import com.wangjiegulu.rapidooo.library.compiler.util.AnnoUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.EasyType;
import com.wangjiegulu.rapidooo.library.compiler.util.ElementUtil;
import com.wangjiegulu.rapidooo.library.compiler.util.LogUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

/**
 * Author: wangjie
 * Email: tiantian.china.2@gmail.com
 * Date: 11/04/2018.
 */
public class TargetElement {
    private FromEntry fromEntry;
    private OOO oooAnno;
    private boolean isPoolUsed;
    private Element generatorClassEl;

    private String targetClassPackage;
    private String targetClassSimpleName;

    private Element fromElement;
    private String fromSuffix;
    private String suffix;

    private String targetSupperTypeId;
    private TypeName targetSupperType = TypeName.OBJECT;

    private TypeMirror poolMethodType;

    /**
     * key: field name
     */
    private Map<String, FromField> allFromFields = new LinkedHashMap<>();

    public void setFromElement(Element fromElement) {
        this.fromElement = fromElement;

        List<? extends Element> eles = fromElement.getEnclosedElements();
        for (Element e : eles) {
            if (ElementKind.FIELD == e.getKind()) {
                if (MoreElements.hasModifiers(Modifier.STATIC).apply(e)) {
                    continue;
                }
                FromField fromField = new FromField();
                fromField.setFieldOriginElement(e);
                allFromFields.put(e.getSimpleName().toString(), fromField);
            }
        }
    }

    public void parseBase() {
        if (null != oooAnno) {
            String specialSuffix = oooAnno.suffix();
            if (!AnnoUtil.oooParamIsNotSet(specialSuffix)) {
                suffix = specialSuffix;
            }

            String specialFromSuffix = oooAnno.fromSuffix();
            if (!AnnoUtil.oooParamIsNotSet(specialFromSuffix)) {
                fromSuffix = specialFromSuffix;
            }
        }

        String fromClassName = fromElement.getSimpleName().toString();
        targetClassPackage = generatorClassEl.getEnclosingElement().toString();
        // eg. replace "BO" when generate VO
        targetClassSimpleName =
                (AnnoUtil.oooParamIsNotSet(fromSuffix) ? fromClassName : fromClassName.substring(0, fromClassName.length() - fromSuffix.length()))
                        + suffix;
    }

    public void parse() {
        if (null == oooAnno) {
            return;
        }

        OOOConversion[] oooConversions = oooAnno.conversion();
        for (OOOConversion oooConversion : oooConversions) {
            String fieldName = oooConversion.fieldName();
            FromField fromField = allFromFields.get(fieldName);
            if (null == fromField) {
                throw new RuntimeException("Field[" + fieldName + "] is not exist in " + MoreElements.asType(fromElement).getQualifiedName());
            }

            FromFieldConversion fromFieldConversion = new FromFieldConversion();
            fromFieldConversion.setOwnerTargetElement(this);
            fromFieldConversion.setOooConversionAnno(oooConversion);
            fromFieldConversion.setOwnerFromField(fromField);
            fromFieldConversion.parse();
            fromField.setFromFieldConversion(fromFieldConversion);
            fromField.setOwnerTargetElement(this);
            fromField.parse();
        }

        targetSupperTypeId = oooAnno.targetSupperTypeId();
        targetSupperType = getFromTargetSupperTypeMirror(oooAnno);

        TypeMirror poolSpecialMethodType = getPoolMethodTypeMirror(oooAnno.pool());
        poolMethodType = ElementUtil.isSameType(poolSpecialMethodType, Object.class) ? generatorClassEl.asType() : poolSpecialMethodType;

        isPoolUsed = isPoolUsedInternal();
    }

    private TypeMirror getPoolMethodTypeMirror(OOOPool pool) {
        try {
            pool.poolMethodClass();
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        throw new RuntimeException("getPoolMethodTypeMirror error");
    }

    public void setOooAnno(OOO oooAnno) {
        this.oooAnno = oooAnno;
    }


    public Element getFromElement() {
        return fromElement;
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

    public String getTargetSupperTypeId() {
        return targetSupperTypeId;
    }

    public TypeName getTargetSupperType() {
        return targetSupperType;
    }

    private TypeName getFromTargetSupperTypeMirror(OOO ooo) {
        // if already id set
        String targetSupperTypeId = ooo.targetSupperTypeId();
        TargetElement temp;
        if (!AnnoUtil.oooParamIsNotSet(targetSupperTypeId) && null != (temp = fromEntry.getFromElementById(targetSupperTypeId))) {
            return EasyType.bestGuess(temp.getTargetClassFullName());
        }
        // else targetType
        try {
            ooo.targetSupperType();
        } catch (MirroredTypeException mte) {
            return TypeName.get(mte.getTypeMirror());
        }
        throw new RuntimeException("getFromTargetSupperTypeMirror error");
    }

    public boolean isTargetSupperTypeId() {
        return null != targetSupperTypeId && !AnnoUtil.oooParamIsNotSet(targetSupperTypeId);
    }

    public boolean isPoolUsed() {
        return isPoolUsed;
    }

    public TypeMirror getPoolMethodType() {
        return poolMethodType;
    }

    private boolean isPoolUsedInternal() {
        if (null == oooAnno) {
            return false;
        }
        OOOPool oooPool = oooAnno.pool();

        boolean acquireMethodSet = !AnnoUtil.oooParamIsNotSet(oooPool.acquireMethod());
        boolean releaseMethodSet = !AnnoUtil.oooParamIsNotSet(oooPool.releaseMethod());
        if (!acquireMethodSet && !releaseMethodSet) {
            return false;
        }
        if (acquireMethodSet && releaseMethodSet) {
            checkPoolMethodValidate();
            return true;
        } else {
            LogUtil.logger("Both AcquireMethod and ReleaseMethodSet need to be set。");
            return false;
        }
    }

    private void checkPoolMethodValidate() {
        List<? extends Element> elements = MoreTypes.asElement(poolMethodType).getEnclosedElements();
        OOOPool oooPool = oooAnno.pool();
        String acquireMethodName = oooPool.acquireMethod();
        String releaseMethodName = oooPool.releaseMethod();
        boolean acquireMethodValidate = false;
        boolean releaseMethodValidate = false;
        for (Element e : elements) {
            if (ElementKind.METHOD == e.getKind()) {
                ExecutableElement methodElement = MoreElements.asExecutable(e);
                // public & static
                if (!MoreElements.hasModifiers(Modifier.STATIC).apply(methodElement)
                        ||
                        !MoreElements.hasModifiers(Modifier.PUBLIC).apply(methodElement)
                        ) {
                    continue;
                }

                if (ElementUtil.equals(methodElement.getSimpleName().toString(), acquireMethodName)) {
                    if (
                            methodElement.getParameters().size() == 0 &&
                                    ElementUtil.isSameSimpleName(methodElement.getReturnType(), targetClassSimpleName)
                            ) {
                        acquireMethodValidate = true;
                        if (releaseMethodValidate) {
                            break;
                        }
                    }
                }
                if (ElementUtil.equals(methodElement.getSimpleName().toString(), releaseMethodName)) {
                    if (methodElement.getParameters().size() != 1) {
                        continue;
                    }

                    if (
                            methodElement.getParameters().size() == 1
                                    &&
                                    ElementUtil.equals(
                                            MoreTypes.asTypeElement(methodElement.getParameters().get(0).asType()).getSimpleName().toString(),
                                            targetClassSimpleName
                                    )
                                    &&
                                    ElementUtil.isSameType(methodElement.getReturnType(), TypeName.VOID)
                            ) {
                        releaseMethodValidate = true;
                        if (acquireMethodValidate) {
                            break;
                        }
                    }
                }
            }
        }

        if (!acquireMethodValidate) {
            throw new RuntimeException("No such acquire method \n[public static "
                    + targetClassSimpleName + " "
                    + acquireMethodName + "()] \n"
                    + " for OBJECT POOL in "
                    + MoreTypes.asTypeElement(poolMethodType).getQualifiedName());
        }

        if (!releaseMethodValidate) {
            throw new RuntimeException("No such release method \n[public static void "
                    + releaseMethodName + "(" + targetClassSimpleName + ")] \n"
                    + " for OBJECT POOL in "
                    + MoreTypes.asTypeElement(poolMethodType).getQualifiedName());
        }

    }
}
